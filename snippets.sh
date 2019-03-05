dir=src/main/resources/snippets
CERT=~/Desktop/eternitas/eternitaskeys1.pem
SCPTARGET=bitnami@34.204.6.252:/home/bitnami/apps/wordpress/htdocs/snippets

for snippet in $(ls  $dir/*-frag.html)
do
scp -i $CERT  $snippet  $SCPTARGET
done

