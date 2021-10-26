import log4js from "log4js";

export function getLogger(level: string) {
  const logger = log4js.getLogger();
  logger.level = level
  return logger;
}