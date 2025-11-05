variable "cluster_name" {
  description = "Name of the ECS cluster"
  type        = string
  default     = "psychological-healthcare-cluster"
}

variable "vpc_id" {
  description = "VPC ID where ECS will be deployed"
  type        = string
}

variable "private_subnets" {
  description = "List of private subnet IDs for ECS tasks"
  type        = list(string)
}

variable "vpc_cidr" {
  description = "VPC CIDR block for security group rules"
  type        = string
}

# Infrastructure Service Images
variable "zookeeper_image" {
  description = "Docker image for Zookeeper"
  type        = string
  default     = "confluentinc/cp-zookeeper:latest"
}

variable "kafka_image" {
  description = "Docker image for Kafka"
  type        = string
  default     = "confluentinc/cp-kafka:latest"
}

variable "mongodb_image" {
  description = "Docker image for MongoDB"
  type        = string
  default     = "mongo:6.0"
}

variable "zipkin_image" {
  description = "Docker image for Zipkin"
  type        = string
  default     = "openzipkin/zipkin:latest"
}

variable "keycloak_image" {
  description = "Docker image for Keycloak"
  type        = string
  default     = "quay.io/keycloak/keycloak:24.0.2"
}

# Infrastructure Services Resources
variable "zookeeper_cpu" {
  description = "CPU units for Zookeeper"
  type        = number
  default     = 256
}

variable "zookeeper_memory" {
  description = "Memory for Zookeeper"
  type        = number
  default     = 512
}

variable "kafka_cpu" {
  description = "CPU units for Kafka"
  type        = number
  default     = 512
}

variable "kafka_memory" {
  description = "Memory for Kafka"
  type        = number
  default     = 1024
}

variable "mongodb_cpu" {
  description = "CPU units for MongoDB"
  type        = number
  default     = 512
}

variable "mongodb_memory" {
  description = "Memory for MongoDB"
  type        = number
  default     = 1024
}

variable "zipkin_cpu" {
  description = "CPU units for Zipkin"
  type        = number
  default     = 256
}

variable "zipkin_memory" {
  description = "Memory for Zipkin"
  type        = number
  default     = 512
}

variable "keycloak_cpu" {
  description = "CPU units for Keycloak"
  type        = number
  default     = 512
}

variable "keycloak_memory" {
  description = "Memory for Keycloak"
  type        = number
  default     = 1024
}

# Business Service Resource Configuration
variable "small_service_cpu" {
  description = "CPU for small business services"
  type        = number
  default     = 512
}

variable "small_service_memory" {
  description = "Memory for small business services"
  type        = number
  default     = 1024
}

variable "medium_service_cpu" {
  description = "CPU for medium business services"
  type        = number
  default     = 512
}

variable "medium_service_memory" {
  description = "Memory for medium business services"
  type        = number
  default     = 1536
}

variable "large_service_cpu" {
  description = "CPU for large business services"
  type        = number
  default     = 1024
}

variable "large_service_memory" {
  description = "Memory for large business services"
  type        = number
  default     = 2048
}

# Service Classification
variable "small_services" {
  description = "List of small services"
  type        = list(string)
  default     = ["appointment", "follow", "attendance", "comment", "collection"]
}

variable "medium_services" {
  description = "List of medium services"
  type        = list(string)
  default     = ["user", "community", "post", "message", "notification", "topic"]
}

variable "large_services" {
  description = "List of large services"
  type        = list(string)
  default     = ["gateway", "media", "recommended", "config-server", "discovery"]
}