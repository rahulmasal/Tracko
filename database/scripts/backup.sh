#!/bin/bash
# Tracko PostgreSQL Backup Script
# Usage: ./backup.sh [database_name] [output_directory]

set -euo pipefail

DB_NAME="${1:-tracko_db}"
BACKUP_DIR="${2:-./backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/tracko_${DB_NAME}_${TIMESTAMP}.sql.gz"
LOG_FILE="${BACKUP_DIR}/backup_${TIMESTAMP}.log"

# Configuration - override with environment variables
DB_HOST="${PG_HOST:-localhost}"
DB_PORT="${PG_PORT:-5432}"
DB_USER="${PG_USER:-tracko_user}"
DB_PASSWORD="${PG_PASSWORD:-tracko_pass_2024}"

mkdir -p "${BACKUP_DIR}"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "${LOG_FILE}"
}

log "Starting backup of database: ${DB_NAME}"

export PGPASSWORD="${DB_PASSWORD}"

pg_dump \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USER}" \
    -d "${DB_NAME}" \
    --no-owner \
    --no-acl \
    --verbose \
    --format=custom \
    --file="${BACKUP_FILE}" \
    2>> "${LOG_FILE}"

if [ $? -eq 0 ]; then
    BACKUP_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
    log "Backup completed successfully: ${BACKUP_FILE} (${BACKUP_SIZE})"

    # Remove backups older than 30 days
    find "${BACKUP_DIR}" -name "tracko_*.sql.gz" -mtime +30 -delete
    log "Cleaned up backups older than 30 days"
else
    log "ERROR: Backup failed!"
    exit 1
fi

unset PGPASSWORD
exit 0
