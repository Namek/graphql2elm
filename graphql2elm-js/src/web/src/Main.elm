module Main exposing (..)

import Data.Settings as Settings exposing (Settings)
import Html exposing (..)
import Html.Attributes as Attr exposing (style, type_, value)
import Html.Events exposing (onClick, onInput)
import Json.Decode as Decode exposing (Value)
import Misc exposing (..)
import Ports exposing (elmCodeGenerationResult, generateElmCode)


main : Program Value Model Msg
main =
    Html.programWithFlags
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }


type alias MainFlags =
    Maybe { query : String, schema : String }



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    elmCodeGenerationResult ElmCodeGenerationResult



-- MODEL


type alias Model =
    { settings : Settings
    , elmCode : Maybe String
    }


init : Value -> ( Model, Cmd Msg )
init json =
    let
        model =
            { settings = json |> Settings.decodeFromJson
            , elmCode = Nothing
            }
    in
    ( model, Cmd.none )



-- UPDATE


type Msg
    = ToggleNullableInComment
    | ToggleEmitMaybeForNullableFields
    | ChangeTypePrefix String
    | SetSchema String
    | SetQuery String
    | GenerateElmCode
    | ElmCodeGenerationResult (Maybe String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    let
        { settings } =
            model
    in
    case msg of
        ToggleNullableInComment ->
            { model | settings = { settings | representNullableInEmittedGraphQLComment = not settings.representNullableInEmittedGraphQLComment } } => Cmd.none

        ToggleEmitMaybeForNullableFields ->
            { model | settings = { settings | emitMaybeForNullables = not settings.emitMaybeForNullables } } => Cmd.none

        ChangeTypePrefix prefix ->
            { model | settings = { settings | typePrefix = prefix } } => Cmd.none

        SetSchema schema ->
            { model | settings = { settings | schema = schema } } => Cmd.none

        SetQuery query ->
            { model | settings = { settings | query = query } } => Cmd.none

        GenerateElmCode ->
            model => generateElmCode (settings |> Settings.encode)

        ElmCodeGenerationResult res ->
            { model | elmCode = res }
                => Cmd.none



-- VIEW


view : Model -> Html Msg
view model =
    let
        { settings } =
            model
    in
    div [ style [ "padding-left" => "20px" ] ]
        [ div []
            [ viewTextarea "Schema" settings.schema SetSchema
            , viewTextarea "Query" settings.query SetQuery
            ]
        , div []
            [ checkbox
                ToggleNullableInComment
                "Represent nullable in comment with generated GraphQL query"
                settings.representNullableInEmittedGraphQLComment
            ]
        , div []
            [ checkbox
                ToggleEmitMaybeForNullableFields
                "Emit Maybe for nullable fields"
                settings.emitMaybeForNullables
            ]
        , label []
            [ text "Generated type prefix:"
            , input
                [ type_ "text"
                , onInput ChangeTypePrefix
                , value settings.typePrefix
                , style [ "width" => "30px", "margin-left" => "10px" ]
                ]
                []
            ]
        , div [] [ button [ onClick GenerateElmCode ] [ text "\x1F937 Generate Elm" ] ]
        , viewIf (model.elmCode /= Nothing)
            (div []
                [ h1 [] [ text "Result" ]
                , pre [] [ text <| Maybe.withDefault "" model.elmCode ]
                ]
            )
        ]


checkbox : msg -> String -> Bool -> Html msg
checkbox msg name isChecked =
    label []
        [ input [ type_ "checkbox", onClick msg, Attr.checked isChecked ] []
        , text name
        ]


viewTextarea : String -> String -> (String -> msg) -> Html msg
viewTextarea headerText val msg =
    div [ style [ "display" => "inline-block" ] ]
        [ h1 [] [ text headerText ]
        , textarea [ onInput msg, Attr.cols 50, Attr.rows 20, value val ] []
        ]
