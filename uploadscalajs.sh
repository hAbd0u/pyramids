TARGET=target/scala-2.12
WEBAPP=src/main/webapp
declare -a SCRIPTS=(
    "eternitas-wordpress-opt.js"
    "eternitas-wordpress-opt.js.map"
    "eternitas-wordpress-jsdeps.js")


source ~/env.sh
sbt fullOptJS
for script in "${SCRIPTS[@]}" 
do
cp   $TARGET/$script  $WEBAPP/js
done
#cp src/main/js/eternitas.js  $WEBAPP/js
