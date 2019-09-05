# oereb-client-gwt

```
mvn clean spring-boot:run
mvn gwt:generate-module gwt:codeserver

mvn gwt:generate-module gwt:devmode 
(java.xml.bind)

mvn package


(mvn clean gwt:generate-module gwt:compile)
```

maven local löschen...
http://127.0.0.1:9876/ -> "Clean"


Testen:
```
<source path="model">
    <exclude name="**/ObjectFactory.*" />
</source>
```