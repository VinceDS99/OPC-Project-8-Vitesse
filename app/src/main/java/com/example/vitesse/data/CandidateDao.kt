package com.example.vitesse.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    @Query("SELECT * FROM candidates ORDER BY id ASC")
    fun getAllCandidates(): Flow<List<Candidate>>

    @Query("SELECT * FROM candidates WHERE isFavorite = 1 ORDER BY lastName ASC, firstName ASC")
    fun getFavoriteCandidates(): Flow<List<Candidate>>

    @Query("SELECT * FROM candidates WHERE id = :candidateId")
    suspend fun getCandidateById(candidateId: Long): Candidate?

    @Query("SELECT * FROM candidates WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%'")
    fun searchCandidates(searchQuery: String): Flow<List<Candidate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: Candidate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidates(candidates: List<Candidate>)

    @Update
    suspend fun updateCandidate(candidate: Candidate)

    @Delete
    suspend fun deleteCandidate(candidate: Candidate)

    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()
}