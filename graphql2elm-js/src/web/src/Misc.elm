module Misc exposing (delayMsg, either, noCmd, viewIf)

import Html exposing (Html, text)
import Process
import Task


viewIf : Bool -> Html msg -> Html msg
viewIf cond el =
    if cond then
        el

    else
        text ""


either : a -> a -> Bool -> a
either a1 a2 cond =
    if cond then
        a1

    else
        a2


delayMsg : Float -> msg -> Cmd msg
delayMsg milliseconds msg =
    Process.sleep milliseconds
        |> Task.perform (always msg)


noCmd : model -> ( model, Cmd msg )
noCmd model =
    ( model, Cmd.none )
