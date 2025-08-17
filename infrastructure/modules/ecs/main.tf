resource "aws_ecs_cluster" "main" {
  name = var.cluster_name

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = var.cluster_name
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "ecsTaskExecutionRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy_attachment" {
    role = aws_iam_role.ecs_task_execution_role.name
    policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Security Groups Infrastructure Services
resource "aws_security_group" "infrastructure_services" {
    name = "infrastructure-services-sg"
    description = "Security group for infrastructure services"
    vpc_id = var.vpc_id

    # Zookeeper
    ingress {
        description = "Zookeeper"
        from_port = 2181
        to_port = 2181
        protocol = "tcp"
        cidr_blocks = [var.vpc_cidr]
    }

    # Kafka
    ingress {
        description = "Kafka"
        from_port = 9092
        to_port = 9092
        protocol = "tcp"
        cidr_blocks = [var.vpc_cidr]
    }

    # MongoDB
    ingress {
        description = "MongoDB"
        from_port = 27017
        to_port = 27017
        protocol = "tcp"
        cidr_blocks = [var.vpc_cidr]
    }

    # Zipkin
    ingress {
        description = "Zipkin"
        from_port = 9411
        to_port = 9411
        protocol = "tcp"
        cidr_blocks = [var.vpc_cidr]
    }

    # Keycloak
    ingress {
        description = "Keycloak"
        from_port = 8080
        to_port = 8080
        protocol = "tcp"
        cidr_blocks = [var.vpc_cidr]
    }

}