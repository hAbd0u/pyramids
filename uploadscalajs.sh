TARGET=target/scala-2.12
CERT=~/Desktop/eternitas/eternitaskeys1.pem
SCPTARGET=bitnami@34.204.6.252:/home/bitnami/apps/wordpress/htdocs/js/
WEBAPP=src/main/webapp
declare -a SCRIPTS=(
    "eternitas-wordpress-opt.js"
    "eternitas-wordpress-opt.js.map"
    "eternitas-wordpress-jsdeps.js")


source ~/env.sh
sbt fullOptJS
for script in "${SCRIPTS[@]}" 
do
#aws s3 cp $TARGET/$script s3://data.lyrx.de/js/$script
#scp -i $CERT  $TARGET/$script  $SCPTARGET
cp   $TARGET/$script  $WEBAPP/js
done 
#scp -i $CERT src/main/js/eternitas.js   $SCPTARGET
cp src/main/js/eternitas.js  $WEBAPP/js
