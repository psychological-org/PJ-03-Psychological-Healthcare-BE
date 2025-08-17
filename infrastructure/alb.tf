# ALB FOR JENKINS SERVER
resource "aws_security_group" "alb_jenkins" {
  name        = "alb-jenkins-sg"
  description = "Security group for Jenkins ALB"
  vpc_id      = module.vpc.vpc_id
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "alb-jenkins-sg"
  }
}

resource "aws_lb" "jenkins" {
  name               = "jenkins-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_jenkins.id]
  subnets           = module.vpc.public_subnets
  tags = {
    Name = "jenkins-alb"
  }
}

resource "aws_lb_target_group" "jenkins" {
  name        = "jenkins-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = module.vpc.vpc_id
  target_type = "instance"
  health_check {
    path                = "/login"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_listener" "jenkins_https" {
  load_balancer_arn = aws_lb.jenkins.arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = "arn:aws:acm:ap-southeast-1:381491880852:certificate/41680820-41fa-4eb3-a7c0-32efb32a3a9f"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.jenkins.arn
  }
}

resource "aws_lb_listener" "jenkins_http" {
  load_balancer_arn = aws_lb.jenkins.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_target_group_attachment" "jenkins" {
  target_group_arn = aws_lb_target_group.jenkins.arn
  target_id        = aws_instance.jenkins.id
  port             = 8080
}


# ALB FOR INFRASTRUCTURE SERVICE
resource "aws_security_group" "alb_services" {
  name        = "alb-services-sg"
  description = "Security group for Services ALB"
  vpc_id      = module.vpc.vpc_id
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "alb-services-sg"
  }
}

resource "aws_lb" "services" {
  name               = "services-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_services.id]
  subnets           = module.vpc.public_subnets
  tags = {
    Name = "services-alb"
  }
}

# Security Group cho ECS Services
resource "aws_security_group" "ecs_from_alb" {
  name        = "ecs-from-services-alb-sg"
  description = "Allow traffic from Services ALB to ECS Services"
  vpc_id      = module.vpc.vpc_id
  
  # Keycloak
  ingress {
    description = "Keycloak from Services ALB"
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    security_groups = [aws_security_group.alb_services.id]
  }

  # Zipkin
  ingress {
    description = "Zipkin from Services ALB"
    from_port = 9411
    to_port = 9411
    protocol = "tcp"
    security_groups = [aws_security_group.alb_services.id]
  }

  # Gateway
  ingress {
    description = "Gateway from Services ALB"
    from_port = 8222
    to_port = 8222
    protocol = "tcp"
    security_groups = [aws_security_group.alb_services.id]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "ecs-from-services-alb-sg"
  }
}

# Target Group
resource "aws_lb_target_group" "keycloak" {
  name = "keycloak-tg"
  port = 8080
  protocol = "HTTP"
  vpc_id = module.vpc.vpc_id
  target_type = "ip"

  health_check {
    enabled = true
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    path = "/health"
    matcher = "200, 404"
    port = "traffic-port"
    protocol = "HTTP"
  }

  tags = {
    Name = "keycloak-tg"
  }
}

resource "aws_lb_target_group" "zipkin" {
  name = "zipkin-tg"
  port = 9411
  protocol = "HTTP"
  vpc_id = module.vpc.vpc_id
  target_type = "ip"

  health_check {
    enabled = true
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    path = "/health"
    matcher = "200"
    port = "traffic-port"
    protocol = "HTTP"
  }

  tags = {
    Name = "zipkin-tg"
  }
}

resource "aws_lb_target_group" "gateway" {

  name = "gateway-tg"
  port = 8222
  protocol = "HTTP"
  vpc_id = module.vpc.vpc_id
  target_type = "ip"

  health_check {
    enabled = true
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    path = "/actuator/health"
    matcher = "200"
    port = "traffic-port"
    protocol = "HTTP"
  }

  tags = {
    Name = "gateway-tg"
  }

}


# Services ALB Listeners - Keycloak
resource "aws_lb_listener" "keycloak_https" {
  load_balancer_arn = aws_lb.services.arn
  port = 443
  protocol = "HTTPS"
  certificate_arn = "arn:aws:acm:ap-southeast-1:381491880852:certificate/41680820-41fa-4eb3-a7c0-32efb32a3a9f"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.keycloak.arn
  }
}


# Services ALB Listeners - Zipkin
resource "aws_lb_listener" "zipkin_https" {
  load_balancer_arn = aws_lb.services.arn
  port = 443
  protocol = "HTTPS"
  certificate_arn = "arn:aws:acm:ap-southeast-1:381491880852:certificate/41680820-41fa-4eb3-a7c0-32efb32a3a9f"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.zipkin.arn
  }
}

# Services ALB Listeners - Gateway
resource "aws_lb_listener" "api_https" {
  load_balancer_arn = aws_lb.services.arn
  port = 443
  protocol = "HTTPS"
  certificate_arn = "arn:aws:acm:ap-southeast-1:381491880852:certificate/41680820-41fa-4eb3-a7c0-32efb32a3a9f"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.gateway.arn
  }
}

# HTTP Direct cho tat ca cac services
resource "aws_lb_listener" "services_http" {
  load_balancer_arn = aws_lb.services.arn
  port = 80
  protocol = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port = "443"
      protocol = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}


