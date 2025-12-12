package com.example.vitesse.data

import kotlinx.coroutines.flow.Flow

class CandidateRepository(private val candidateDao: CandidateDao) {

    val allCandidates: Flow<List<Candidate>> = candidateDao.getAllCandidates()

    val favoriteCandidates: Flow<List<Candidate>> = candidateDao.getFavoriteCandidates()

    fun searchCandidates(query: String): Flow<List<Candidate>> {
        return candidateDao.searchCandidates(query)
    }

    suspend fun getCandidateById(id: Long): Candidate? {
        return candidateDao.getCandidateById(id)
    }

    suspend fun insert(candidate: Candidate): Long {
        return candidateDao.insertCandidate(candidate)
    }

    suspend fun update(candidate: Candidate) {
        candidateDao.updateCandidate(candidate)
    }

    suspend fun delete(candidate: Candidate) {
        candidateDao.deleteCandidate(candidate)
    }
}