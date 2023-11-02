package com.dpfht.demofbasemvvm.framework.di.module

import com.dpfht.demofbasemvvm.domain.repository.AppRepository
import com.dpfht.demofbasemvvm.domain.usecase.AddBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.AddBookUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.DeleteBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.DeleteBookUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.FetchConfigsUseCase
import com.dpfht.demofbasemvvm.domain.usecase.FetchConfigsUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMQuotaUseCase
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMQuotaUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMTokenUseCase
import com.dpfht.demofbasemvvm.domain.usecase.FetchFCMTokenUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetAllBooksUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetAllBooksUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetBookUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamBookStateUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamBookStateUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamConfigsUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamConfigsUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMQuotaUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMQuotaUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMTokenUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamFCMTokenUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamLoginStateUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamLoginStateUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamPushMessageUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetStreamPushMessageUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.GetUserProfileUseCase
import com.dpfht.demofbasemvvm.domain.usecase.GetUserProfileUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.IsLoginUseCase
import com.dpfht.demofbasemvvm.domain.usecase.IsLoginUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.LogEventUseCase
import com.dpfht.demofbasemvvm.domain.usecase.LogEventUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.LogOutUseCase
import com.dpfht.demofbasemvvm.domain.usecase.LogOutUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.PostFCMMessageUseCase
import com.dpfht.demofbasemvvm.domain.usecase.PostFCMMessageUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.ResendVerificationCodeUseCase
import com.dpfht.demofbasemvvm.domain.usecase.ResendVerificationCodeUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.SetFCMQuotaUseCase
import com.dpfht.demofbasemvvm.domain.usecase.SetFCMQuotaUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.SignInWithGoogleUseCase
import com.dpfht.demofbasemvvm.domain.usecase.SignInWithGoogleUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.StartPhoneNumberVerificationUseCase
import com.dpfht.demofbasemvvm.domain.usecase.StartPhoneNumberVerificationUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.UpdateBookUseCase
import com.dpfht.demofbasemvvm.domain.usecase.UpdateBookUseCaseImpl
import com.dpfht.demofbasemvvm.domain.usecase.VerifyPhoneNumberWithCodeUseCase
import com.dpfht.demofbasemvvm.domain.usecase.VerifyPhoneNumberWithCodeUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

  @Provides
  fun provideIsLoginUseCase(appRepository: AppRepository): IsLoginUseCase {
    return IsLoginUseCaseImpl(appRepository)
  }

  @Provides
  fun provideLogEventUseCase(appRepository: AppRepository): LogEventUseCase {
    return LogEventUseCaseImpl(appRepository)
  }

  @Provides
  fun provideFetchConfigsUseCase(appRepository: AppRepository): FetchConfigsUseCase {
    return FetchConfigsUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamConfigsUseCase(appRepository: AppRepository): GetStreamConfigsUseCase {
    return GetStreamConfigsUseCaseImpl(appRepository)
  }

  @Provides
  fun provideSignInWithGoogleUseCase(appRepository: AppRepository): SignInWithGoogleUseCase {
    return SignInWithGoogleUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamLoginStatusUseCase(appRepository: AppRepository): GetStreamLoginStateUseCase {
    return GetStreamLoginStateUseCaseImpl(appRepository)
  }

  @Provides
  fun provideStartPhoneNumberVerificationUseCase(appRepository: AppRepository): StartPhoneNumberVerificationUseCase {
    return StartPhoneNumberVerificationUseCaseImpl(appRepository)
  }

  @Provides
  fun provideVerifyPhoneNumberWithCodeUseCase(appRepository: AppRepository): VerifyPhoneNumberWithCodeUseCase {
    return VerifyPhoneNumberWithCodeUseCaseImpl(appRepository)
  }

  @Provides
  fun provideResendVerificationCodeUseCase(appRepository: AppRepository): ResendVerificationCodeUseCase {
    return ResendVerificationCodeUseCaseImpl(appRepository)
  }

  @Provides
  fun provideLogOutUseCase(appRepository: AppRepository): LogOutUseCase {
    return LogOutUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetUserProfileUseCase(appRepository: AppRepository): GetUserProfileUseCase {
    return GetUserProfileUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamPushMessageUseCase(appRepository: AppRepository): GetStreamPushMessageUseCase {
    return GetStreamPushMessageUseCaseImpl(appRepository)
  }

  @Provides
  fun provideFetchFCMTokenUseCase(appRepository: AppRepository): FetchFCMTokenUseCase {
    return FetchFCMTokenUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamFCMTokenUseCase(appRepository: AppRepository): GetStreamFCMTokenUseCase {
    return GetStreamFCMTokenUseCaseImpl(appRepository)
  }

  @Provides
  fun providePostFCMMessageUseCase(appRepository: AppRepository): PostFCMMessageUseCase {
    return PostFCMMessageUseCaseImpl(appRepository)
  }

  @Provides
  fun provideFetchFCMQuotaUseCase(appRepository: AppRepository): FetchFCMQuotaUseCase {
    return FetchFCMQuotaUseCaseImpl(appRepository)
  }

  @Provides
  fun provideSetFCMQuotaUseCase(appRepository: AppRepository): SetFCMQuotaUseCase {
    return SetFCMQuotaUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamFCMQuotaUseCase(appRepository: AppRepository): GetStreamFCMQuotaUseCase {
    return GetStreamFCMQuotaUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetStreamBookStateUseCase(appRepository: AppRepository): GetStreamBookStateUseCase {
    return GetStreamBookStateUseCaseImpl(appRepository)
  }

  @Provides
  fun provideAddBookUseCase(appRepository: AppRepository): AddBookUseCase {
    return AddBookUseCaseImpl(appRepository)
  }

  @Provides
  fun provideUpdateBookUseCase(appRepository: AppRepository): UpdateBookUseCase {
    return UpdateBookUseCaseImpl(appRepository)
  }

  @Provides
  fun provideDeleteBookUseCase(appRepository: AppRepository): DeleteBookUseCase {
    return DeleteBookUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetAllBooksUseCase(appRepository: AppRepository): GetAllBooksUseCase {
    return GetAllBooksUseCaseImpl(appRepository)
  }

  @Provides
  fun provideGetBookUseCase(appRepository: AppRepository): GetBookUseCase {
    return GetBookUseCaseImpl(appRepository)
  }
}
