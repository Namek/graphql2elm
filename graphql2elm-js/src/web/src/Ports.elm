port module Ports exposing (elmCodeGenerationError, elmCodeGenerationResult, generateElmCode, selectText_Pre, selectText_TextArea)

import Array exposing (Array)
import Json.Encode exposing (Value)



-- OUT


port generateElmCode : Value -> Cmd msg


port selectText_Pre : String -> Cmd msg


port selectText_TextArea : String -> Cmd msg



-- IN


port elmCodeGenerationResult : (String -> msg) -> Sub msg


port elmCodeGenerationError : (Array String -> msg) -> Sub msg
