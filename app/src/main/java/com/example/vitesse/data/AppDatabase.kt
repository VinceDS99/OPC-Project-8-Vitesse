package com.example.vitesse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Candidate::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun candidateDao(): CandidateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vitesse_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.candidateDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(candidateDao: CandidateDao) {
            // Supprimer tout (pour le développement)
            candidateDao.deleteAllCandidates()

            // Insérer 8 candidats de test
            val candidates = listOf(
                Candidate(
                    firstName = "Jean",
                    lastName = "Dupont",
                    phoneNumber = "+33 6 12 34 56 78",
                    email = "jean.dupont@email.com",
                    dateOfBirth = "15/03/1990",
                    expectedSalary = 45000.0,
                    notes = "Développeur full-stack avec 5 ans d'expérience en Java et Kotlin. Maîtrise de Spring Boot et Android. Passionné par les nouvelles technologies.",
                    profilePhotoUrl = null
                ),
                Candidate(
                    firstName = "Marie",
                    lastName = "Martin",
                    phoneNumber = "+33 6 23 45 67 89",
                    email = "marie.martin@email.com",
                    dateOfBirth = "22/07/1988",
                    expectedSalary = 52000.0,
                    notes = "Chef de projet agile avec 8 ans d'expérience. Certifiée Scrum Master. Excellente communication et gestion d'équipe.",
                    profilePhotoUrl = null
                ),
                Candidate(
                    firstName = "Thomas",
                    lastName = "Bernard",
                    phoneNumber = "+33 6 34 56 78 90",
                    email = "thomas.bernard@email.com",
                    dateOfBirth = "10/11/1992",
                    expectedSalary = 38000.0,
                    notes = "Designer UX/UI créatif avec un portfolio impressionnant. Maîtrise de Figma, Adobe XD et Sketch. Sensible à l'accessibilité.",
                    profilePhotoUrl = null
                ),
                Candidate(
                    firstName = "Sophie",
                    lastName = "Petit",
                    phoneNumber = "+33 6 45 67 89 01",
                    email = "sophie.petit@email.com",
                    dateOfBirth = "05/01/1995",
                    expectedSalary = 42000.0,
                    notes = "Data scientist spécialisée en machine learning. Doctorat en statistiques. Expérience avec Python, TensorFlow et PyTorch.",
                    profilePhotoUrl = null,
                    isFavorite = true
                ),
                Candidate(
                    firstName = "Lucas",
                    lastName = "Robert",
                    phoneNumber = "+33 6 56 78 90 12",
                    email = "lucas.robert@email.com",
                    dateOfBirth = "18/09/1991",
                    expectedSalary = 48000.0,
                    notes = "Ingénieur DevOps avec expertise en Kubernetes, Docker et CI/CD. Certifié AWS Solutions Architect.",
                    profilePhotoUrl = null
                ),
                Candidate(
                    firstName = "Emma",
                    lastName = "Richard",
                    phoneNumber = "+33 6 67 89 01 23",
                    email = "emma.richard@email.com",
                    dateOfBirth = "30/04/1993",
                    expectedSalary = 40000.0,
                    notes = "Développeuse frontend passionnée par React et Vue.js. Sensible aux performances et à l'optimisation. Connaissance en accessibilité web.",
                    profilePhotoUrl = null,
                    isFavorite = true
                ),
                Candidate(
                    firstName = "Antoine",
                    lastName = "Durand",
                    phoneNumber = "+33 6 78 90 12 34",
                    email = "antoine.durand@email.com",
                    dateOfBirth = "12/12/1989",
                    expectedSalary = 55000.0,
                    notes = "Architecte logiciel senior avec 10 ans d'expérience. Expertise en microservices et architecture cloud. Mentor apprécié.",
                    profilePhotoUrl = null
                ),
                Candidate(
                    firstName = "Chloé",
                    lastName = "Moreau",
                    phoneNumber = "+33 6 89 01 23 45",
                    email = "chloe.moreau@email.com",
                    dateOfBirth = "25/06/1994",
                    expectedSalary = 43000.0,
                    notes = "Ingénieure QA avec passion pour l'automatisation des tests. Expérience en Selenium, Appium et JUnit. Rigueur et attention aux détails.",
                    profilePhotoUrl = null
                )
            )

            candidateDao.insertCandidates(candidates)
        }
    }
}