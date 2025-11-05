output "db_instance_id" {
  description = "RDS instance ID"
  value       = aws_db_instance.postgresql.id
}

output "db_endpoint" {
  description = "RDS instance endpoint (for application connection)"
  value       = aws_db_instance.postgresql.endpoint
}

output "db_port" {
  description = "RDS instance port"
  value       = aws_db_instance.postgresql.port
}

output "db_username" {
  description = "Database master username"
  value       = var.db_username
  sensitive   = true
}

output "connection_string" {
  description = "Example connection string for applications"
  value       = "postgresql://${var.db_username}:[PASSWORD]@${aws_db_instance.postgresql.endpoint}:${aws_db_instance.postgresql.port}/[DATABASE_NAME]"
}