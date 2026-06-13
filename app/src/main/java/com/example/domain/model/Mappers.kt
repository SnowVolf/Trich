package com.example.domain.model

import com.example.model.BoardSetting
import com.example.model.CatalogThread
import com.example.model.FileInfo
import com.example.model.PostInfo

fun BoardSetting.toDomain() = Board(
    id = id,
    name = name,
    category = category,
    info = info,
    bumpLimit = bumpLimit
)

fun FileInfo.toDomain(baseUrl: String) = Attachment(
    path = baseUrl + path,
    fullname = fullname,
    thumbnail = baseUrl + thumbnail,
    type = type,
    size = size
)

fun CatalogThread.toDomain(baseUrl: String) = ThreadSummary(
    num = num,
    subject = subject,
    comment = comment,
    files = files?.map { it.toDomain(baseUrl) } ?: emptyList()
)

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
