# Consul Server
**Setup instructions for running Consul server**

Running Consul as a first class citizen while dealing with docker client nodes, and git2consul is a bit more
complicated than I'd like it to be. Until I find a proper way to solve things, this will server
as a "copy and paste" style guide. 

## Server setup
Running the `setup.sh` script on a clean EC2 AWS Linux AMI (ami-71867013). This should be added to a instances
USER_DATA or a ECS Launch configuration... Chef/Puppet, or baked AMI via Packer... Anything will do.

