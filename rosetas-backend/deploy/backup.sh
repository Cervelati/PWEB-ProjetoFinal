#!/bin/sh
# Backup diário do banco e das fotos. Uso: ./deploy/backup.sh   (rode na pasta rosetas-backend)
# Agende:  crontab -e  ->  30 3 * * * cd /opt/rosetas/rosetas-backend && ./deploy/backup.sh
set -eu
DIR="${BACKUP_DIR:-./backups}"
DIA=$(date +%Y-%m-%d)
mkdir -p "$DIR"
C="docker compose -f docker-compose.prod.yml"

$C exec -T postgres pg_dump -U rosetas -d rosetas | gzip > "$DIR/banco-$DIA.sql.gz"
$C exec -T backend tar -C /data/uploads -czf - . > "$DIR/fotos-$DIA.tar.gz"

# guarda 14 dias
find "$DIR" -type f -mtime +14 -delete
echo "Backup ok em $DIR ($DIA)"
