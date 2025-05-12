
# Structure and architecture
The automata explorer is a full-stack application with  Full-stack application with a Spring Boot backend and a React + Vite frontend. The backend is responsible for all graph manipulations including building the difference machine based on the [gLTSdiff library](https://github.com/TNO/gLTSdiff). The frontend is responsible for graph visualization and is build around the  [CytoScape library](https://js.cytoscape.org/).

## Project Structure

```plaintext
AutomataExplorer/
│
├── backend/              # Spring Boot backend project
│   ├── src/              # Backend source code
│   ├── pom.xml           # Maven configuration
│   └── Dockerfile        # Dockerfile for backend
│
├── frontend/             # React frontend project
│   ├── src/              # Frontend source code
│   ├── package.json      # npm configuration
│   └── Dockerfile        # Dockerfile for frontend
│
├── docker-compose.yml    # Docker Compose configuration
├── .gitignore            # Git ignore rules
├── .dockerignore         # Docker ignore rules
└── README.md             # Root for project documentation
```


## Backend
The Spring Boot backend exposes its functionality via a RESTfull api allowing clients to interact over HTTP using JSON. 

### Basic application flow
1. The client post a `sessionRequest` with the reference, subject and type.
2. The backend controller receives the request, delegates it to appropriate services and responds with a `sessionResponse`. The response includes a list with possible processing options for type. 
3. The client post a `buildRequest` which can include (additional) processing actions. 
4. The backend controller delegates the request to the *buildService*. The *buildservices* get the correct handler from the *handlerService* and processes the build request `buildResponse` is returned. 
5. Step 3 and 4 are repeated until the client is done and sent a `DELETE` with the `sessionId` to the sessions endpoint.
6. The server terminates the session by clearing all the data and sends a `sessionResponse`. 

A detailed API description for the backend services with all the endpoints and messages can be found [here](/docs/api.md).


### Controller
The `restcontoller` handles all incoming HTTP request and acts as an entry point for the backend application. In the controller endpoints are mapped methods and request are orchestrated and delegated to the appropriate services. To decouple the internal (domain) model from the external API `Data Transfer Objects (DTOs)` are used. The `DTOs` define the JSON structures for the request and response messages used by the controller. 


### Services 
The `service` layer contains the core logic for the Automata Explorer. The services generalized and designed with type-based components to support extensibility.

- `parserService`: parse the inputs `.dot` using the [Graphviz](https://graphviz.org/docs/layouts/dot/) dot tool.   
- `sessionService`: implements a session, allowing users to work in multiple tabs simultaneously. 
- `buildservice`: handles the generalized build procedure including pre and post processing. 
- `handlerService`: helper service to find the handler based on the automata type. 
 
### Domain

#### Handlers
The buildService uses the HandlerService to resolve and delegate to the correct `DiffHandler<T>` implementation based on the requested type. The handler follows the *strategy design pattern* with `DiffHandler<T>` as the strategy interface. This enables a generalized build processes for different type of inputs and makes it easier to extend the application for other types or build configurations. The concrete strategies are implemented in `MealyDiffHandler` and `StringDiffHandler`. The abstract `AbstractDiffHandler` contains the common pre and postprocessing steps and collects the processors for the given type. 

#### Processors, ProcessingModal and Rules
The processor applies a transformation to a  difference Automaton based on the
specific (user requested) processing action and also follows the *strategy design pattern*. The  `DiffAutomatonProcessor<T>` provides a common interface for processing automaton of different types. Each implementation serves a specific processing actions for a certain type. The correct processors are selected at runtime via the appropriate handler. 

The `ProcessingModel` contains the enumeration types and data structures for the processing actions. The valid combinations for processing actions for a given type are defined globally in the `ProcessingRules`.    

#### Model (Mealy)
- `Mealy`: represents a Mealy transition with an input and an output which can be uses a a transition property in the `DiffAutomaton<Mealy>`.
- `MealyCombiner`: exends the `Combiner<Mealy>` to ensure combining and matching in gLTSDiff library. 
- `MergedMealy`: Represents a merged Mealy transition with an input and two outputs and is used for additional merging of transitions in `MealyMerger`.

### API documentation
- The backend is equipped with an interactive Swagger UI for exploring and testing the REST API. The UI is available at `http://localhost:8080/swagger-ui/index.html#/`. This UI will show all available endpoints as well as all the request and response formats. The API can also be used from the browser directly. The openApi file can be downloaded via  `http://localhost:8080/v3/api-docs`. 
- The backend the Grapviz dot tool to parse the input files. All files which conform to this specification will be parsed successfully.


### Tests 
Automatic testing is part of the overall applications build pipeline using gitHub actions. About 60 test cases are crafted to validate the backend implementation. 

- The controller is test using the `@webMvcTest`to validate the HTTPS request and response handling in isolation. 
- The services are tested to validate the common business logic using `@SpringBootTest`, `Mockito` and `JUnit`. 
- Integration test are performed to combine multiple behaviors to validate real workflows and edge cases. 

## Frontend
The frontend is built using React and Vite and written in typescript. The frontend communicates with the backend by exchanging messages over HTTP with JSON. 

### Basic application flow
1. The user uploads to `.dot` files on the `uploadPage` and selects regular or mealy processing. 
2. The frontend sends the request to the backend and initiates the build without any processing actions.
3. The frontend routes to the `VisualizationPage` loads the visualization and displays the build results. 
4. The user can filter, change layout, select context menu actions and or modify the modal. 
5. The user exits the application, the fronent sent a session close request to the backend.  

### API
- `SessionApi.tsx`: Utility for sending messages to the backend API and to enforce proper exception handling. 
- `RequestResponse`: Contains all interfaces for the messages exchanged with the backend. 


### Hooks
- `useSession`: Custom react hook that manages the state and encapsulates all communication with the backend API by leveraging the `sessionApi,tsx`. 
- `useTransform`: Custom react hook to transform backend responses to usable data for the visualization. It decouples raw API data from rendering and visualization logic. 

### Pages
- `uploadPage`: The initial [page](/docs/img/UploadPage.png) where the user van upload the `.dot`files and select the type for processing. 
- `visualizationPage`: The [page](/docs/img/VisualizationPage.png) where the actual visualization is shown.  

### Components
- `InfoModal`: a general Modal to be used in the application which can handle different inner contents such as `AboutContent`, `ActionContent`, `BuildContent` and `ElementContent`.  
- `CytoscapeVisualization`: The action visualization component. To prevent the file to be bloated congifuration are implemented in the utils classes `attachContextCollapse`, `attachCytoscapeMenus`, `cytoScapeStyles` and `exportPdfs`.  
- `DragAndDrop`: a drag and drop file upload element to be used in the `UploadPage`.  