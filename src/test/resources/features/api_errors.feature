# language: ru
@negative @weather
Функция: Обработка ошибок API текущей погоды

  Правило: при некорректном запросе API возвращает {"error": {"code", "message"}} с соответствующим HTTP статусом

    Структура сценария: Ошибка "<scenario>" возвращается согласно документации API
      Дано зарегистрирована заглушка ошибки "<scenario>"
      Когда отправлен запрос текущей погоды
      Тогда HTTP статус ответа равен <status>
      И код ошибки в теле ответа равен <errorCode>
      И сообщение об ошибке совпадает с ожидаемым "<errorMessage>"

      Примеры:
        | scenario           | status | errorCode | errorMessage                                |
        | missing_q          | 400    | 1003      | Parameter q is missing.                     |
        | location_not_found | 400    | 1006      | No matching location found                  |
        | invalid_api_key    | 401    | 2006      | API key provided is invalid                 |
        | quota_exceeded     | 403    | 2007      | API key has exceeded calls per month quota  |