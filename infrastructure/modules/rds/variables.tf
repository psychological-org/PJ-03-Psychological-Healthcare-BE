variable "vpc_id" {
    description = "VPC ID where RDS will be deployed"
    type        = string
}

variable "vpc_cidr" {
    description = "VPC CIDR block"  
    type        = string
}

variable "private_subnets" {
    description = "List of private subnet IDs for RDS"
    type        = list(string)
}

variable "db_username" {
    description = "Database master username"
    type        = string
    default     = "root"
}

variable "db_password" {
    description = "Database master password"
    type        = string
    sensitive   = true
}