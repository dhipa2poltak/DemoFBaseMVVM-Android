package com.dpfht.demofbasemvvm.domain.usecase

import com.dpfht.demofbasemvvm.domain.entity.VoidResult

interface LogEventUseCase {

  suspend operator fun invoke(eventName: String, param: Map<String, String> = mapOf()): VoidResult
}
