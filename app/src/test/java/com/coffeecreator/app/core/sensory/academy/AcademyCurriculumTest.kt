package com.coffeecreator.app.core.sensory.academy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AcademyCurriculumTest {

    @Test
    fun testAll22LessonsPresentAndBilingual() {
        val lessons = AcademyLessonRegistry.allLessons
        assertEquals(22, lessons.size)
        assertEquals(
            (1..22).map { "EDU-" + it.toString().padStart(2, '0') },
            lessons.map { it.id }
        )

        for (i in 1..22) {
            val id = "EDU-%02d".format(i)
            val lesson = AcademyLessonRegistry.findLessonById(id)
            assertNotNull("Lesson $id must exist", lesson)
            assertEquals(i, lesson!!.moduleNumber)
            assertFalse("English title must not be blank", lesson.titleEn.isBlank())
            assertFalse("Indonesian title must not be blank", lesson.titleId.isBlank())
            assertTrue("Lesson must have English objectives", lesson.objectivesEn.isNotEmpty())
            assertTrue("Lesson must have Indonesian objectives", lesson.objectivesId.isNotEmpty())
            assertTrue("Lesson must have English core concepts", lesson.coreConceptsEn.isNotEmpty())
            assertTrue("Lesson must have Indonesian core concepts", lesson.coreConceptsId.isNotEmpty())
            assertFalse("English content must not be blank", lesson.contentMarkdownEn.isBlank())
            assertFalse("Indonesian content must not be blank", lesson.contentMarkdownId.isBlank())
            assertFalse("Accessibility notes EN required", lesson.accessibilityNotesEn.isBlank())
            assertFalse("Accessibility notes ID required", lesson.accessibilityNotesId.isBlank())
            assertTrue("Lesson must have citations", lesson.citations.isNotEmpty())
            assertTrue("Lesson must have practice quiz questions", lesson.practiceQuestions.isNotEmpty())
        }
    }

    @Test
    fun testFiveBasicTastesScientificCorrectionInEdu05() {
        val lesson = AcademyLessonRegistry.findLessonById("EDU-05")
        assertNotNull(lesson)

        // Verify title & core concepts clarify the 5 basic tastes and address the acid myth
        assertTrue(lesson!!.titleEn.contains("Five Basic Tastes"))
        val contentEn = lesson.contentMarkdownEn
        assertTrue(contentEn.contains("SWEET"))
        assertTrue(contentEn.contains("SOUR"))
        assertTrue(contentEn.contains("SALTY"))
        assertTrue(contentEn.contains("BITTER"))
        assertTrue(contentEn.contains("UMAMI"))
        assertTrue(contentEn.contains("\"ACID\" IS NOT A 6TH TASTE"))

        val contentId = lesson.contentMarkdownId
        assertTrue(contentId.contains("LIMA rasa dasar"))
        assertTrue(contentId.contains("Mengapa \"Acid\" Bukan Rasa Keenam?"))

        // Verify quiz question reinforces this
        val quiz = lesson.practiceQuestions.first()
        val correctOption = quiz.options.first { it.isCorrect }
        assertTrue(correctOption.textEn.contains("sourness is the basic taste"))
    }

    @Test
    fun testOrganicAcidAtlasSevenAcids() {
        val acids = OrganicAcidAtlas.allAcids
        assertEquals(7, acids.size)

        val expectedIds = listOf(
            "acid_citric",
            "acid_malic",
            "acid_tartaric",
            "acid_acetic",
            "acid_lactic",
            "acid_phosphoric",
            "acid_quinic"
        )
        expectedIds.forEach { id ->
            val acid = OrganicAcidAtlas.findAcidById(id)
            assertNotNull("Acid $id must be present", acid)
            assertFalse(acid!!.chemicalFormula.isBlank())
            assertTrue(acid.molarMassGPerMol > 0.0)
            assertTrue(acid.pKa1 > 0.0)
            assertTrue(acid.standardTestingConcentrationGramsPerLiter > 0.0)
            assertFalse(acid.safeReferencePreparationNotesEn.isBlank())
        }
    }

    @Test
    fun testQGraderPathwayCredentialLiteracyEdu22() {
        val lesson = AcademyLessonRegistry.findLessonById("EDU-22")
        assertNotNull(lesson)

        assertTrue(lesson!!.titleEn.contains("Q Grader Pathways"))
        val contentEn = lesson.contentMarkdownEn
        assertTrue(contentEn.contains("grant CQI Q Grader or R Grader credentials"))

        val contentId = lesson.contentMarkdownId
        assertTrue(contentId.contains("pengganti sertifikasi resmi CQI Q Grader"))

        val quiz = lesson.practiceQuestions.first()
        val correct = quiz.options.first { it.isCorrect }
        assertTrue(correct.textEn.contains("requires in-person examination at a CQI-accredited laboratory"))
    }
}
