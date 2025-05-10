### This will be here while Github doesn't support img tags in mermaid diagrams
- And is intended to be used for previewing with the recommended VSC extensions referenced at the docs [README](./README.md). You can also use [Mermaid chart](https://www.mermaidchart.com/) to explore Mermaid

### Tech Use Throughout The App (simplified)

```mermaid
%% TD -> Top-Down
    flowchart TD

%% Nodes
    %% Normal Nodes
        Root([RootScreen])
        HS(HomeScreen 🏠)
        AS(AchievementsScreen 🏅)
        SC(SettingsScreen 🔧)
        FAB{"<p style="font-size: 10px;"> User </br> Actions"}

    %% Icon Nodes
        DB("<img style="max-height: 50px; object-fit: contain" src='./imgs/db.png'> Room DB")
        FB("<img style="max-height: 50px; object-fit: contain" src="https://firebase.google.com/static/images/brand-guidelines/logo-logomark.png"> Firebase")
        FB_store("<img style="max-height: 50px; object-fit: contain" src="https://firebase.google.com/static/images/products/icons/build_firestore.svg"> Firestore")
        FB_auth("<img style="max-height: 50px; object-fit: contain" src="https://firebase.google.com/static/images/products/icons/build_auth.svg"> Authentication")
        FB_functions("<img style="max-height: 50px; object-fit: contain" src="https://firebase.google.com/images/products/icons/build_functions.svg"> Functions")


        PD("<img style="max-height: 50px; object-fit: contain" src='./imgs/db-encrypted.png'> Preferences </br> DataStore")

        MP("<img style="max-height: 50px; object-fit: contain" src='../app/src/main/res/drawable/mediapipe.png'> MediaPipe")
        GEM("<img style="max-height: 50px; object-fit: contain" src='../app/src/main/res/drawable/gemini.png'> Gemini")
        KTOR("<img style="max-height: 50px; object-fit: contain" src="https://resources.jetbrains.com/storage/products/company/brand/logos/Ktor_icon.png"> Ktor")

%% Connections
    Root <==> AS
    Root <==> HS
    Root <==> SC
    HS --- FAB 

    FAB -.- MP
    FAB -.- GeminiApiService

    subgraph GeminiApiService
        direction LR
        KTOR <-- HTTP --> GEM
    end
    
    AS <-- Your Achievements --> DB
    AS <-- Leaderboard --> FB

    FB --- FB_store
    FB --- FB_functions
    FB --- FB_auth

    SC <--> PD

%% Styling. #0d1117 = github dark color
    %% Definitions
    classDef screenStyle color:#FFFFFF, stroke:#00C853
    classDef noBackgroundStyle color:#FFFFFF, fill:#0d1117

    %% Definitions applied
    class HS,SC,AS screenStyle
    class DB,FB,FB_store,FB_auth,FB_functions,PD,MP,GEM,KTOR,GeminiApiService noBackgroundStyle

    %% Direct style definitions
    style FAB color:#FFFFFF, fill:#163a9e 
```
### Source Code Structure

```mermaid
graph TD

%% Root packages and files
    DI("<img style="max-height: 20px; object-fit: contain" src="https://insert-koin.io/img/koin_new_logo.png"> DependencyInjection.kt")
    Log("<img style="max-height: 20px; object-fit: contain" src="./imgs/logs.png"> Logging.kt")
    A("<img style="max-height: 20px; object-fit: contain" src="https://developer.android.com/static/images/brand/android-head_flat.png"> android")
    D("<img style="max-height: 20px; object-fit: contain" src="./imgs/tools.png"> data")
    U("<img style="max-height: 20px; object-fit: contain" src="./imgs/user-interface.png"> ui")

%% Sub packages
    A --> A1(activities)
    A --> A2(utils)
    A --> A3(viewmodels)
    A --> A4(MyApplication.kt)

    A1 --> A1_1(utils)
    A2 --> A2_1(camera)
    A3 --> A3_1(utils)

    D --> D1(domains)
    D --> D2(sources)
    D --> D4(utils)

    D1 --> D1_1(firebase)
    D1 --> D1_2(gemini)
    D1 --> D1_3(mediapipe)
    D2 --> D2_1(client)
    D2 --> D2_2(cloud)
    D2 --> D2_3(local)

    D2_2 --> D2_2_1(gemini)
    D2_2 --> D2_2_2(firebase)

    D2_3 --> D2_3_1(database)
    D2_3 --> D2_3_2(preferences)

    U --> U1(animations)
    U --> U2(components)
    U --> U3(screens)
    U --> U4(theme)

    U2 --> U2_2(utils)

    U3 --> U3_1(achievements)
    U3 --> U3_2(home)
    U3 --> U3_3(root)
    U3 --> U3_4(settings)

    U3_2 --> D3_2_1(outline)
    U3_2 --> D3_2_2(chat)

    U3_1(achievements) --> U3_1_1(local)
    U3_1(achievements) --> U3_1_2(leaderboard)

%% Styles
    classDef noBackgroundStyle color:#FFFFFF, fill:#0d1117

    class DI,Log,A,D,U noBackgroundStyle
```