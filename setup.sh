#!/bin/bash
set -e

echo "=== 1. Verificando/Criando projeto Maven ==="
if [ ! -f "pom.xml" ]; then
    mvn archetype:generate \
        -DgroupId=com.atividade \
        -DartifactId=selenium-projeto \
        -DarchetypeArtifactId=maven-archetype-quickstart \
        -DarchetypeVersion=1.4 \
        -DinteractiveMode=false
    cd selenium-projeto
fi

echo "=== 2. Adicionando Selenium 4.25.0 ao pom.xml ==="
if ! grep -q "selenium-java" pom.xml; then
    python3 -c '
with open("pom.xml", "r") as f:
    content = f.read()

dep = """    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-java</artifactId>
      <version>4.25.0</version>
    </dependency>
  </dependencies>"""

content = content.replace("  </dependencies>", dep, 1)
with open("pom.xml", "w") as f:
    f.write(content)
'
    echo "Dependência adicionada com sucesso."
else
    echo "Dependência do Selenium já existe no pom.xml."
fi

echo "=== 3. Instalando Google Chrome ==="
if ! command -v google-chrome &> /dev/null; then
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg
    sudo sh -c 'echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list'
    sudo apt-get update
    sudo apt-get install -y google-chrome-stable
else
    echo "Google Chrome já está instalado: $(google-chrome --version)"
fi

echo "=== 4. Resolvendo e baixando dependências do Maven ==="
mvn dependency:resolve
mvn test-compile

echo "=== Tudo pronto! ==="