Weather App - Kotlin Android

Um aplicativo Android nativo que fornece previsões meteorológicas em tempo real consumindo a API pública do Open-Meteo.
O projeto foi desenvolvido com foco em arquitetura limpa, performance e uma experiência de usuário fluida.

🚀 Funcionalidades
•Busca por Cidade: Converte nomes de cidades em coordenadas geográficas (Geocoding).
•Clima em Tempo Real: Exibe temperatura atual e descrição das condições climáticas.
•Sistema de Cache Inteligente: Armazena resultados em memória por 1 hora, economizando bateria e dados do usuário.
•Tratamento de Erros Robusto: Lida com falta de internet, cidades não encontradas e caracteres especiais (acentos/espaços).
•UI Reativa: Interface que se atualiza automaticamente conforme os dados chegam da rede.

🛠️ Tecnologias e Bibliotecas
•Kotlin: Linguagem principal do projeto.
•Coroutines & Flow: Para processamento assíncrono e gerenciamento de estados sem travar a UI.
•ViewModel & Lifecycle KTX: Gerenciamento de ciclo de vida e sobrevivência a rotações de tela.
•OkHttp: Cliente HTTP para comunicação eficiente com a API.
•Open-Meteo API: Fonte de dados meteorológicos (sem necessidade de chave API).
•JSON (org.json): Parsing manual de dados para manter o projeto leve.

📁 Estrutura do Projeto
O projeto segue o padrão de arquitetura sugerido pelo Google:

com.sousa.weather/
├── model/           # WeatherResult (Sealed Class para estados de Sucesso/Erro)
├── network/         # WeatherApi e Interface (Comunicação com OkHttp)
├── repository/      # WeatherRepository (Lógica de Cache e Regras de Negócio)
├── viewmodel/       # WeatherViewModel (Ponte entre Dados e UI)
└── MainActivity.kt  # Entrada do App e Observação de Dados

🧠 Decisões de Implementação
1. Estratégia de CachePara evitar chamadas repetidas à API para a mesma cidade, implementamos um Map no Repositório que valida o tempo de expiração:

Kotlinif (cachedEntry != null && (currentTime - cachedEntry.timestamp) < 1_HOUR) {
   return cached
Entry.data // Retorna do cache
   }

2. Segurança e Robustez
•URLEncoding: Nomes de cidades como "São Paulo" são codificados para garantir que a URL da API não quebre com espaços ou acentos.
•HTTPS: Toda a comunicação é feita de forma criptografada.
•Dispatchers.IO: As chamadas de rede são explicitamente movidas para threads secundárias, garantindo 0% de travamento na interface.

🔧 Como Rodar o Projeto
1.Clone este repositório.
2.Certifique-se de ter o Android Studio Ladybug ou superior.
3.Adicione a permissão de internet no seu AndroidManifest.xml:

```xml <uses-permission android:name="android.permission.INTERNET" />```

4.Sincronize o Gradle para baixar as dependências do `lifecycle-viewmodel-ktx` e `okhttp`.
5.Execute em um emulador ou dispositivo físico com API 24+.

## 📝 Licença
Este projeto utiliza a API do [Open-Meteo](https://open-meteo.com/) sob a licença [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).

---
*Desenvolvido como projeto de estudo de arquitetura Android Moderna.*

6.Execute em um emulador ou dispositivo físico com API 24+.

📝 Licença
Este projeto utiliza a API do Open-Meteo sob a licença CC BY 4.0.Desenvolvido como projeto de estudo de arquitetura Android Moderna.