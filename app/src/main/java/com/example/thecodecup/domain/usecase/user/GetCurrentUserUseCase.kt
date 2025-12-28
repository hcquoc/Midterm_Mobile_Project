package com.example.thecodecup.domain.usecase.user

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.User
import com.example.thecodecup.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Use case to get the current local user
 * Since there's no authentication, we treat this as a single local user
 */
class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {

    /**
     * Execute the use case to observe the current user
     * @return Flow of DomainResult containing User
     */
    operator fun invoke(): Flow<DomainResult<User>> {
        return userRepository.observeUser()
            .map<User, DomainResult<User>> { user ->
                DomainResult.Success(user)
            }
            .catch { exception ->
                emit(
                    DomainResult.Error(
                        DomainException.DatabaseException(
                            message = "Failed to load user: ${exception.message}",
                            cause = exception
                        )
                    )
                )
            }
    }

    /**
     * Get current user without observing (one-time fetch)
     * @return DomainResult containing User
     */
    suspend fun getOnce(): DomainResult<User> {
        return try {
            val user = userRepository.getUser()
            DomainResult.Success(user)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to load user: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

