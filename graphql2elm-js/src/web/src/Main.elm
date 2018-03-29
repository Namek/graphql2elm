module Main exposing (..)

import Array
import Data.SchemaQuery exposing (introspectionQuery)
import Data.Settings as Settings exposing (Settings)
import Html exposing (..)
import Html.Attributes as Attr exposing (style, type_, value)
import Html.Events exposing (onClick, onInput)
import Json.Decode as Decode exposing (Value)
import Maybe.Extra exposing (isJust)
import Misc exposing (..)
import Ports exposing (..)
import Time


main : Program Value Model Msg
main =
    Html.programWithFlags
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.batch <|
        [ elmCodeGenerationResult ElmCodeGenerationResult
        , elmCodeGenerationError (Array.toList >> ElmCodeGenerationError)
        ]



-- CONSTS


( resultCodeElementId, schemaQueryElementId ) =
    ( "result-code", "schema-query" )



-- MODEL


type alias Model =
    { settings : Settings
    , elmCode : Maybe String
    , errors : Maybe (List String)
    , isSchemaQueryExpanded : Bool
    }


init : Value -> ( Model, Cmd Msg )
init json =
    let
        model =
            { settings = json |> Settings.decodeFromJson
            , elmCode = Nothing
            , errors = Nothing
            , isSchemaQueryExpanded = False
            }
    in
    ( model, Cmd.none )



-- UPDATE


type Msg
    = ToggleSchemaQuery
    | SelectSchemaQuery
    | ToggleNullableInComment
    | ToggleEmitMaybeForNullableFields
    | ChangeTypePrefix String
    | SetSchema String
    | SetQuery String
    | GenerateElmCode
    | ElmCodeGenerationResult String
    | ElmCodeGenerationError (List String)
    | SelectGeneratedElmCode


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    let
        { settings } =
            model
    in
    case msg of
        ToggleSchemaQuery ->
            { model | isSchemaQueryExpanded = not model.isSchemaQueryExpanded }
                => (if not model.isSchemaQueryExpanded then
                        delayMsg (Time.millisecond * 150) SelectSchemaQuery
                    else
                        Cmd.none
                   )

        SelectSchemaQuery ->
            model => selectText_TextArea schemaQueryElementId

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
            { model | elmCode = Just res, errors = Nothing }
                => Cmd.none

        ElmCodeGenerationError errors ->
            { model | elmCode = Nothing, errors = Just errors }
                => Cmd.none

        SelectGeneratedElmCode ->
            model => selectText_Pre resultCodeElementId



-- VIEW


view : Model -> Html Msg
view model =
    let
        { settings } =
            model
    in
    div [ style [ "padding-left" => "20px" ] ]
        [ h1 [] [ text "GraphQL ➡ Elm" ]
        , div []
            [ div []
                [ if model.isSchemaQueryExpanded then
                    button [ onClick ToggleSchemaQuery ] [ text "Collapse" ]
                  else
                    button [ onClick ToggleSchemaQuery ] [ text "➡ Use this query to get schema from backend" ]
                , viewIf model.isSchemaQueryExpanded <|
                    div []
                        [ textarea
                            [ Attr.id schemaQueryElementId
                            , Attr.cols 30
                            , Attr.rows 20
                            , value introspectionQuery
                            ]
                            []
                        ]
                ]
            , viewTextarea "Schema" settings.schema SetSchema
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
        , div []
            [ button [ onClick GenerateElmCode ] [ text "\x1F937 Generate Elm" ]
            , viewIf (isJust model.elmCode) <|
                button [ onClick SelectGeneratedElmCode ] [ text "Select generated code" ]
            ]
        , viewIf (model.errors /= Nothing)
            (div []
                [ h1 [] [ text "Errors" ]
                , pre []
                    (model.errors
                        |> Maybe.andThen (List.map ((++) "\n" >> text) >> Just)
                        |> Maybe.withDefault []
                    )
                ]
            )
        , viewIf (model.elmCode /= Nothing)
            (div []
                [ h1 [] [ text "Result" ]
                , pre [ Attr.id resultCodeElementId ] [ text <| Maybe.withDefault "" model.elmCode ]
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
    div
        [ style [ "display" => "inline-block" ] ]
        [ h1 [] [ text headerText ]
        , textarea
            [ onInput msg
            , Attr.cols 50
            , Attr.rows 20
            , value val
            , Attr.placeholder <| "Paste your " ++ headerText ++ " here..."
            ]
            []
        ]
