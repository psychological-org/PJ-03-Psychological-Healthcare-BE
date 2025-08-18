module "rds" {
    source = "./modules/rds"

    vpc_id = module.vpc.vpc_id
    vpc_cidr = var.vpc_cidr
    private_subnets = module.vpc.private_subnets

    db_username = var.db_username
    db_password = var.db_password

}
