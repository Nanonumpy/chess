# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAEYAdE8z2oEAK7YAxAAsAMwAHABMAJwgMH7I9gAWYDoI3oYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD03gZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeoBAAMQWG4zm6mAjIWywjUC8dQNUyVxqDIfl8jV6ChZk4mDtfPUPqqXpQ9TQ3gQCATFPurbpqlqIFV6Ld7IGPO52nt7fuxlqCg4HE10u0g+9w4XbbHE+D6IU3j1GOAZ-ic5b+9HgpXa43p71HsRu-uMKeZexaLxaj7LAvzhDs-TLV5DSVJZ6n2EFLz1doIHrNBoOWS4E0oDsUwweownTdNswmSDPhgGDgWWeD4kQ5DUP2K50A4UxPB8fxoHYRkYhFOAI2kOAFBgAAZCAskKbDmCdah-WaNoul6Ax1HyNBs0VOY1l+f4OCuMDBWA-0IIrT5IRBdSdi+aFHhAiSqCRGAURxdEoAxISRIJIkwFJd9DD3GkDwZJlpxUrlbx8+9l2FMUJTdGU5XLd4lUwFVgw1N0jAgNQYDQCBmCtNFgt5UKrJs3t+x3LzfWqf0R35KMYzjQptOTZBUxgDMAkI3NVHzeYYOLUt6jeDKspgAAzHxOEbBi8sXAUk3KrtXVnbdEvVGAmn0P4dlS9LMuYY4wBAeJStAiqy2ZaYr2gJAAC8UA4GqUFjRSMPhRqSjANN0ycdr+S6wsxl66B+vOvVLpu3Z6ObTzBSq+kjzkFAX3iC8rxvGGZsqR91wDFHtyh2aHlhf1nPFDJVEAzBdJeuaaheIiDPmUi0O+SjqIbRm6OerCmpwmA8II0Y6bikiyOZq9WZQ9n0MmjwvF8PxPBQdAYjiRIFaV5yfCwMTBTA+oGmkCMBIjdoI26Hp5NURThhZpD0E5z8LP9G3kIpx34WpmyEGEzWnO9s9XLUdyju8-L+T8sBEeRhDbbQecQodMLajgeIUBADVEYS1UNQASTQKgTSQZ8rxgcWpoPY75oyvsBzxqz-TOyjQdu+7HvjBrXuajMvoFjrfp6ktAYVYH4ib8Gm0YtGdedbtsdfXHp+hu8w6MFBuBPK8o+vbQ49DpcMeFaRV6ZQwM9rypKfqDX-YAhAgLdiuaZgEYtJOrm3tw-DswhxiZZYvwUQ3H4bA4oNQCTRDAAA4kqDQ2s65lgaBA42Zt7BKmtmLGO9tz5u3qM7O2lMp6V2QDkKBuYMRELACQtQAcSTBypEvekMBGQRw3rg2OZdQr7yTinNOs9DrLRznnAuRc9QlwwZPfGnkezV2DnA10w9R4tzqvbDuPMu7fTzAWfufUh6NygNdW6E1mziI9i6Xhb4F4h2mvUchlDVAYh3tNB8wowE5EgdAmRWDCZlhsdA0m5N8ESJOrTFBuZCwNHGCElA2dpCFicGEIIAQQSbHiLqFAbpORfBBMkUAap0lQSMssSJAA5fJdFOgv0klTeA3N3q80-gLSJqgwkRKVNE2J8TEnLGSakvJhlYLLGyWnXp3V+ljGKaUy45Tx5MVlv4DgAB2CI6YUDphiBGIIcBuIADZ4CTkMJQmARQakEMftJDoyDUHD3FtmcZcwKmJk8d+HB6DkKEVuSM9CASTEzzhuiShGJfkoEodQoOeM6HxwYUwyOrCHHl0TsnVO6cryZySjAXO+dkDCP1KXcRhVTHFRrhY2Rkp5F6LBoop67dKhiQ+t3HMP1NFFgHmWbwpL9Fj0msYzs1lTGI3MV2ReEKxwwEBf8yhqN6HoyFKuLGByPEEyeSKvZwKb53y8Q-PSYzWkxPqHEhJ9zML4xpXU-mLS5htN1R07+My-6WFXl7TYyskAJDAHa-sEBHUACkIDijcXMGIgy1RHLeicqSTRmSyR6JEtB0dXmjGwAgYAdqoBwAgF7KAaxInRINe7BVcJnmxvQNmdYibk2pvTXsAA6iwbOJsegACEBIKDgAAaW+FmnVzgOkwHMuqvFM8ABWPq0D-O9STFAhJA4eQseC3ewqoUsJeegWFHDpUIp4RnfhaLBGYt4aIl2uLvlSJKmfbl9c2XkujA9JRVLqnvxap9dRnVGX-WZUDXR7LDET0lScmyfL54Cssb5RhTJ-kdtJOwhOnCIobkiYcrdByOBpQFDtGAOUciQaXP249hLAPU0qpKilbdX4qNqa1J9fcmXaLg6h0arKOVGJ-YEyu4qlpZz9YYHUIiQBpugFWM0tZwz1SJfhssgYBNhmQkR4TlS36d0zBRl9AMywmgEzAOsDZrW0JkJK2ovgtB-KVDOemmbtVqVLZQct0AV1QelU+TcsBIAcflRfKuJUAnEufso6lNSP6mq08xOWngk1OpdcF+UiBgywGANgBNhA8gFEObA0TesDZGxNmbYwmC816V7d+X9piQDcDwPY7TaN6hFai6VrlK5D5r0MB6Q93KirSNPa5glrs+2eZzXJnmfMv7TKAA)
## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
