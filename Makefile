upload:
	rm -rf releases
	sbt publish
	rsync -avzre ssh --partial --progress --delete releases/me fedorapeople.org:public_html/maven/

