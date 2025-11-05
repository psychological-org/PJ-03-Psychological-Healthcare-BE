variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "ap-southeast-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnets" {
  description = "List of public subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnets" {
  description = "List of private subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

variable "azs" {
  description = "Availability Zones"
  type        = list(string)
  default     = ["ap-southeast-1a", "ap-southeast-1b"]
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "root"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  default     = "Admin@123"
  sensitive   = true
}

# MongoDB credentials
variable "mongodb_username" {
  description = "MongoDB username"
  type        = string
  default     = "root"
}

variable "mongodb_password" {
  description = "MongoDB password"
  type        = string
  default     = "Admin@123"
  sensitive   = true
}