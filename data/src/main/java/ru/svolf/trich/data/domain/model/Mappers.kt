package ru.svolf.trich.domain.model

import  ru.svolf.trich.model.BoardSetting
import  ru.svolf.trich.model.CatalogThread
import  ru.svolf.trich.model.FileInfo
import  ru.svolf.trich.model.PostInfo

/**
 * Преобразует сетевую модель настроек доски в доменную модель.
 * @return Доменная модель доски.
 */
fun BoardSetting.toDomain() = Board(
    id = id,
    name = name,
    category = category,
    info = info,
    bumpLimit = bumpLimit,
    enablePosting = enablePosting
)

/**
 * Преобразует информацию о файле из API в доменную модель вложения.
 * @param baseUrl Базовый URL для формирования полных ссылок.
 * @return Доменная модель вложения (Attachment).
 */
fun FileInfo.toDomain(baseUrl: String) = Attachment(
    path = baseUrl + path,
    fullname = fullname,
    thumbnail = baseUrl + thumbnail,
    type = type,
    size = size
)

/**
 * Преобразует поток из каталога API в доменную модель потока (summary).
 * @param baseUrl Базовый URL для формирования полных ссылок вложений.
 * @return Сводка о потоке (ThreadSummary).
 */
fun CatalogThread.toDomain(baseUrl: String) = ThreadSummary(
    num = num,
    subject = subject,
    comment = comment,
    files = files?.map { it.toDomain(baseUrl) } ?: emptyList()
)

/**
 * Преобразует информацию о посте из API в доменную модель поста.
 * @param baseUrl Базовый URL для формирования полных ссылок вложений.
 * @return Доменная модель поста (Post).
 */
fun PostInfo.toDomain(baseUrl: String) = Post(
    num = num,
    parent = parent,
    board = board,
    timestamp = timestamp,
    date = date,
    subject = subject,
    comment = comment,
    op = op,
    files = files?.map { it.toDomain(baseUrl) } ?: emptyList()
)
