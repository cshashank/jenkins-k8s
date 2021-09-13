curVersion=`git tag | tail -1`
docker tag timbergrove/op2nginxfe:latest timbergrove/op2nginxfe:$curVersion
docker push timbergrove/op2nginxfe:$curVersion
