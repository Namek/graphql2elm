# Front-end development

0. Go to root folder
1. Build logic with `gradle assemblyWeb`
2. Run watcher for front-end Elm code

`cd src/web && chokidar "src/**/*.elm" -c "elm-make src/Main.elm --output ../../../build/web/ui.js" --initial`