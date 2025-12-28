package com.example.thecodecup.domain.usecase.user

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.repository.UserRepository

/**
 * Use case to update user profile
 * Validates input before updating
 */
class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {

    /**
     * Data class for update parameters
     */
    data class Params(
        val name: String? = null,
        val phone: String? = null,
        val email: String? = null,
        val address: String? = null
    )

    /**
     * Execute the use case to update user profile
     * @param params The profile fields to update (null fields are ignored)
     * @return DomainResult indicating success or validation/database error
     */
    suspend operator fun invoke(params: Params): DomainResult<Unit> {
        // Validate phone if provided
        if (params.phone != null && params.phone.isBlank()) {
            return DomainResult.Error(
                DomainException.ValidationException(
                    message = "Phone number cannot be empty",
                    field = "phone"
                )
            )
        }

        // Validate name if provided
        if (params.name != null && params.name.isBlank()) {
            return DomainResult.Error(
                DomainException.ValidationException(
                    message = "Name cannot be empty",
                    field = "name"
                )
            )
        }

        return try {
            // Update only the fields that are provided
            params.name?.let { userRepository.updateName(it.trim()) }
            params.phone?.let { userRepository.updatePhone(it.trim()) }
            params.email?.let { userRepository.updateEmail(it.trim()) }
            params.address?.let { userRepository.updateAddress(it.trim()) }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to update user profile: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Convenience method to update all fields at once
     */
    suspend fun updateAll(
        name: String,
        phone: String,
        email: String,
        address: String
    ): DomainResult<Unit> {
        return invoke(
            Params(
                name = name,
                phone = phone,
                email = email,
                address = address
            )
        )
    }
}

