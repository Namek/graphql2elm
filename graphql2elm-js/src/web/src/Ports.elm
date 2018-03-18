port module Ports exposing (..)

import Json.Encode exposing (Value)


port generateElmCode : Value -> Cmd msg


port elmCodeGenerationResult : (Maybe String -> msg) -> Sub msg
