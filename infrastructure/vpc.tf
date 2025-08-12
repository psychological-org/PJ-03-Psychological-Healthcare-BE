module "vpc" {
    source = "./modules/vpc"
    vpc_cidr        = var.vpc_cidr
    public_subnets  = var.public_subnets
    private_subnets = var.private_subnets
    azs             = var.azs
}

resource "aws_eip" "nat" {
  domain = "vpc"
  tags = {
    Name = "main-nat-eip"
  }
}

resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.nat.id
  subnet_id    = module.vpc.public_subnets[0]
  tags = {
    Name = "main-nat-gateway"
  }
}

resource "aws_route_table" "private" {
  vpc_id = module.vpc.vpc_id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }
  tags = {
    Name = "private-route-table"
  }
}

resource "aws_route_table_association" "private" {
  count          = length(module.vpc.private_subnets)
  subnet_id      = module.vpc.private_subnets[count.index]
  route_table_id = aws_route_table.private.id
}