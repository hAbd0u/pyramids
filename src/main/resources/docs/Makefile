include ~/makevars.mk


MYMARKDOWN=$(wildcard *.md)
PDFS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).pdf)
TEXS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).tex)
HTMLS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName))-frag.html)
DOCHTMLS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName))-doc.html)
TXTS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).txt)
DOCXS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).docx)
WIKIS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).wiki)
AWS=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).aws)
BBCODES=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).bbcode)
EPUB=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).epub)
AWSEPUB=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).awsepub)
AWSMOBI=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).awsmobi)
MOBI=$(foreach aName,$(MYMARKDOWN), $(basename $(aName)).mobi)

LUABBCODE=$(RESOURCES)/pandoc/panbbcodeVBulletin.lua
PDF_OPTIONS=--pdf-engine=xelatex 

METADATA=
IMAGES=$(wildcard images/*.*)



%.bbcode: %.md $(METADATA) $(IMAGES)  $(LUABBCODE)
	@$(PANDOC) -f markdown+pipe_tables -t $(LUABBCODE) -o $@  $<
	@echo  $(shell date): $@ 
%.aws: %.md $(METADATA) $(IMAGES) %-doc.html
	aws s3 cp $(basename $<)-doc.html s3://data.lyrx.de
%.awsepub: %.epub
	aws s3 cp  $(basename $<).epub s3://data.lyrx.de/epub/$(basename $<).epub
%.awsmobi: %.mobi
	aws s3 cp  $(basename $<).mobi s3://data.lyrx.de/mobi/$(basename $<).mobi
%.tex: %.org $(METADATA) $(IMAGES)
	@$(PANDOC)  $(PANDOC_OPTIONS) $(METADATA) $< -o $@ 
	@echo  $(shell date): $@ 
%.tex: %.md $(METADATA) $(IMAGES)
	@$(PANDOC)  $(PANDOC_OPTIONS)  $(METADATA) $< -o $@ 
	@echo  $(shell date): $@ 
%-frag.html: %.md $(METADATA) $(IMAGES)
	@$(PANDOC) --webtex $(PANDOC_OPTIONS)  $(METADATA) $< -o $@ 
	@echo  $(shell date): $@ 
%-doc.html: %.md $(METADATA) $(IMAGES)
	@$(PANDOC) --webtex --standalone  $(PANDOC_OPTIONS)  $(METADATA) $< -o $@ 
	@echo  $(shell date): $@ 
%.txt: %.md $(METADATA) $(IMAGES)
	@$(PANDOC) --wrap=none --to=markdown $(PANDOC_OPTIONS)  $(METADATA) $< -o $@
	@echo  $(shell date): $@ 
%.pdf: %.md $(METADATA) $(IMAGES) $(PDF_TEMPLATE)
	@$(PANDOC) $(PDF_OPTIONS)  $(PANDOC_OPTIONS)  $(METADATA) $< -o $@ 
	@echo  $(shell date): $@ 
%.pdf: %.tex $(METADATA)  $(IMAGES)
	@$(PDFLATEX) $<  >> /dev/null
	@$(PDFLATEX) $<  >> /dev/null
	@echo  $(shell date): $@ 
%.wiki: %.md $(METADATA) $(IMAGES)
	@$(PANDOC) -t mediawiki $(PANDOC_OPTIONS)  $(METADATA) $<  -o $@ 
	@echo  $(shell date): $@ 
%.docx: %.md $(METADATA) $(IMAGES)
	@$(PANDOC)  $(PANDOC_OPTIONS)  $(METADATA) $<  -o $@ 
	@echo  $(shell date): $@ 
%.epub: %.md $(METADATA) $(IMAGES)
	@$(PANDOC)  --toc $(PANDOC_OPTIONS)  $(METADATA) $<  -o $@ 
	@echo  $(shell date): $@ 
%.mobi: %.epub $(METADATA) $(IMAGES)
	@ebook-convert   $<   $@  >> /dev/null 
	@echo  $(shell date): $@ 




bbcode: $(BBCODES)
pdf: $(PDFS)
html: $(HTMLS)
dochtml: $(DOCHTMLS)
docx: $(DOCXS)
wiki: $(WIKIS)
tex: $(TEXS)
txt: $(TXTS)

epubs: $(EPUB)
mobis: $(MOBI)
books: epubs mobis

awsepubs: $(AWSEPUB)
awsmobis: $(AWSMOBI)
awsbooks: awsmobis awsepubs
awsimages:
	aws s3 cp images/ s3://data.lyrx.de/images --recursive

mdzip:
	zip md.zip $(MYMARKDOWN)




clean:
	@rm -f  $(PDFS) $(HTMLS) $(DOCHTMLS) $(TEXS) $(WIKIS) $(TXTS) \
                $(DOCXS) $(BBCODES) $(TEXS) $(EPUB) $(MOBI) \
                 md.zip

count:
	@wc -w $(MYMARKDOWN) > wordcount.txt
	@cat wordcount.txt




