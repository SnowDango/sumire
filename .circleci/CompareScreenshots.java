import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * VRT 用に、比較元(base)と PR 側(head)のスクリーンショット集を突き合わせる。
 *
 * <p>使い方: {@code java .circleci/CompareScreenshots.java <base-dir> <head-dir> <out-dir>}
 *
 * <p>base-dir / head-dir 以下の *.png を相対パスで対応付け(collect-screenshots.sh の出力を想定)、
 * 1 ピクセルでも違うもの・head にだけあるもの・base にだけあるものについて
 * <ul>
 *   <li>{@code <out-dir>/images/<name>_compare.png}: 左から base / diff / PR を並べた比較画像
 *   <li>{@code <out-dir>/summary.tsv}: {@code <status>\t<name>\t<detail>}(status は changed / added / deleted)
 * </ul>
 * を書き出す。name は相対パスの "/" を "_" に置き換えて拡張子を外したもの。
 * 差分があっても終了コードは 0(結果は PR コメントで見せる)。画像が読めないときだけ 2 を返す。
 *
 * <p>JDK だけで動くよう、Java 11 の source-file mode で実行できる書き方にしている。
 */
public class CompareScreenshots {

    private static final int MARGIN = 16;
    private static final int GAP = 16;
    private static final int HEADER = 32;
    private static final Color CANVAS = new Color(0xE0, 0xE0, 0xE0);
    private static final Color LABEL = new Color(0x21, 0x21, 0x21);
    private static final int DIFF_COLOR = 0xFFFF0000;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("usage: java CompareScreenshots.java <base-dir> <head-dir> <out-dir>");
            System.exit(1);
        }
        System.setProperty("java.awt.headless", "true");
        Path baseDir = Paths.get(args[0]);
        Path headDir = Paths.get(args[1]);
        Path outDir = Paths.get(args[2]);
        Path imageDir = outDir.resolve("images");
        Files.createDirectories(imageDir);

        TreeMap<String, Path> base = listPngs(baseDir);
        TreeMap<String, Path> head = listPngs(headDir);
        System.out.println("base: " + base.size() + " image(s) in " + baseDir);
        System.out.println("head: " + head.size() + " image(s) in " + headDir);

        TreeSet<String> all = new TreeSet<>(base.keySet());
        all.addAll(head.keySet());

        List<String> summary = new ArrayList<>();
        int failures = 0;
        for (String rel : all) {
            String name = rel.replaceAll("\\.png$", "").replace('/', '_');
            Path out = imageDir.resolve(name + "_compare.png");
            try {
                BufferedImage before = base.containsKey(rel) ? read(base.get(rel)) : null;
                BufferedImage after = head.containsKey(rel) ? read(head.get(rel)) : null;
                String status;
                String detail;
                List<Panel> panels = new ArrayList<>();
                if (before == null) {
                    status = "added";
                    detail = size(after);
                    panels.add(new Panel("after (PR, added)", after));
                } else if (after == null) {
                    status = "deleted";
                    detail = size(before);
                    panels.add(new Panel("before (base, deleted)", before));
                } else {
                    Diff diff = diff(before, after);
                    if (diff.count == 0) {
                        continue;
                    }
                    status = "changed";
                    detail = describe(before, after, diff);
                    panels.add(new Panel("before (base)", before));
                    panels.add(new Panel("diff", diff.image));
                    panels.add(new Panel("after (PR)", after));
                }
                ImageIO.write(compose(panels), "png", out.toFile());
                summary.add(status + "\t" + name + "\t" + detail);
                System.out.println(status + ": " + rel + " (" + detail + ")");
            } catch (IOException e) {
                failures++;
                System.err.println("failed to compare " + rel + ": " + e.getMessage());
            }
        }

        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(outDir.resolve("summary.tsv"), StandardCharsets.UTF_8))) {
            for (String line : summary) {
                w.println(line);
            }
        }
        int unchanged = all.size() - summary.size() - failures;
        System.out.println(summary.size() + " screen(s) differ, " + unchanged + " unchanged, " + failures + " failed");
        if (failures > 0) {
            System.exit(2);
        }
    }

    /** dir 以下の *.png を「dir からの相対パス("/" 区切り) → 実パス」で返す。隠しファイルは除く。 */
    private static TreeMap<String, Path> listPngs(Path dir) throws IOException {
        TreeMap<String, Path> result = new TreeMap<>();
        if (!Files.isDirectory(dir)) {
            return result;
        }
        try (Stream<Path> stream = Files.walk(dir)) {
            List<Path> files = stream
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".png"))
                .filter(p -> !p.getFileName().toString().startsWith("."))
                .collect(Collectors.toList());
            for (Path p : files) {
                result.put(dir.relativize(p).toString().replace('\\', '/'), p);
            }
        }
        return result;
    }

    private static BufferedImage read(Path path) throws IOException {
        BufferedImage image = ImageIO.read(path.toFile());
        if (image == null) {
            throw new IOException("not a readable image: " + path);
        }
        return toArgb(image);
    }

    private static BufferedImage toArgb(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
            return image;
        }
        BufferedImage argb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = argb.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return argb;
    }

    private static final class Diff {
        final BufferedImage image;
        final long count;

        Diff(BufferedImage image, long count) {
            this.image = image;
            this.count = count;
        }
    }

    /**
     * 差分画像を作る。同じピクセルは base を白に寄せて薄く、違うピクセル(サイズ違いではみ出た部分も含む)は赤で塗る。
     */
    private static Diff diff(BufferedImage before, BufferedImage after) {
        int width = Math.max(before.getWidth(), after.getWidth());
        int height = Math.max(before.getHeight(), after.getHeight());
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        long count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean inBefore = x < before.getWidth() && y < before.getHeight();
                boolean inAfter = x < after.getWidth() && y < after.getHeight();
                if (inBefore && inAfter && before.getRGB(x, y) == after.getRGB(x, y)) {
                    out.setRGB(x, y, faded(before.getRGB(x, y)));
                } else {
                    out.setRGB(x, y, DIFF_COLOR);
                    count++;
                }
            }
        }
        return new Diff(out, count);
    }

    /** 白の上に合成したときの明るさを、さらに白へ 75% 寄せたグレーにする。 */
    private static int faded(int argb) {
        double alpha = ((argb >>> 24) & 0xFF) / 255.0;
        double r = ((argb >> 16) & 0xFF) * alpha + 255 * (1 - alpha);
        double g = ((argb >> 8) & 0xFF) * alpha + 255 * (1 - alpha);
        double b = (argb & 0xFF) * alpha + 255 * (1 - alpha);
        double luminance = 0.299 * r + 0.587 * g + 0.114 * b;
        int v = (int) Math.round(255 - (255 - luminance) * 0.25);
        return 0xFF000000 | (v << 16) | (v << 8) | v;
    }

    private static String size(BufferedImage image) {
        return image.getWidth() + "x" + image.getHeight();
    }

    private static String describe(BufferedImage before, BufferedImage after, Diff diff) {
        double total = (double) diff.image.getWidth() * diff.image.getHeight();
        String pixels = String.format(Locale.ROOT, "%d px (%.2f%%)", diff.count, diff.count * 100 / total);
        if (before.getWidth() != after.getWidth() || before.getHeight() != after.getHeight()) {
            return pixels + ", size " + size(before) + " -> " + size(after);
        }
        return pixels;
    }

    private static final class Panel {
        final String label;
        final BufferedImage image;

        Panel(String label, BufferedImage image) {
            this.label = label;
            this.image = image;
        }
    }

    /** パネルを横に並べ、上にラベルを付ける。フォントが使えない環境ではラベル無しで出す。 */
    private static BufferedImage compose(List<Panel> panels) {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        // 画像よりラベルの方が長いときは、ラベルが隣の列に食い込まないよう列幅を広げる
        int[] columns = new int[panels.size()];
        BufferedImage scratch = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measure = scratch.createGraphics();
        int width = MARGIN * 2 + GAP * (panels.size() - 1);
        int height = 0;
        for (int i = 0; i < panels.size(); i++) {
            Panel p = panels.get(i);
            columns[i] = Math.max(p.image.getWidth(), labelWidth(measure, font, p.label));
            width += columns[i];
            height = Math.max(height, p.image.getHeight());
        }
        measure.dispose();
        height += MARGIN * 2 + HEADER;

        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(CANVAS);
        g.fillRect(0, 0, width, height);
        int x = MARGIN;
        int top = MARGIN + HEADER;
        for (int i = 0; i < panels.size(); i++) {
            Panel p = panels.get(i);
            // 背景が透明な Preview も見えるように白の上に置く
            g.setColor(Color.WHITE);
            g.fillRect(x, top, p.image.getWidth(), p.image.getHeight());
            g.drawImage(p.image, x, top, null);
            drawLabel(g, font, p.label, x, MARGIN + HEADER - 10);
            x += columns[i] + GAP;
        }
        g.dispose();
        return out;
    }

    private static int labelWidth(Graphics2D g, Font font, String text) {
        try {
            return g.getFontMetrics(font).stringWidth(text);
        } catch (Throwable t) {
            // headless 環境にフォントが無いと AWT が例外を投げることがある。そのときはラベルを描かない
            return 0;
        }
    }

    private static void drawLabel(Graphics2D g, Font font, String text, int x, int baseline) {
        try {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(font);
            g.setColor(LABEL);
            g.drawString(text, x, baseline);
        } catch (Throwable t) {
            // ラベルは無くても比較はできるので、警告だけ出して続ける
            System.err.println("warning: cannot draw label '" + text + "': " + t);
        }
    }
}
