package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.pattern.*
import com.frankandrobot.rapier.rule.BaseRule
import org.amshove.kluent.shouldEqual
import org.funktionale.option.Option.Some
import org.jetbrains.spek.api.Spek
import java.util.*


class RapierSpec : Spek({
  describe("#rapier") {

    describe("two examples with identical fillers") {

      val blankTemplate = BlankTemplate(
        name = "test",
        slots = slotNames("role")
      )
      val example = Example(
        blankTemplate,
        document,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(wordTokens("Senior", "Software", "Engineer"))
        ))
      )
      val example2 = Example(
        blankTemplate,
        document2,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(wordTokens("Senior", "Software", "Engineer"))
        ))
      )
      val examples = Examples(listOf(example, example2))
      val params = RapierParams(
        Random = Random(20),
        compressionFails = 7,
        metricMinPositiveMatches = 1,
        compressionPriorityQueueSize = 7,
        ruleSizeWeight = 100.0
      )

      it("should find a rule with empty pre/post fillers") {
        val result = rapier(blankTemplate, examples = examples, params = params)
        result[0].learnedRules.map(::toBaseRule) shouldEqual listOf(
          BaseRule(
            preFiller = Pattern(
              PatternList(words("a"), tags("DT"), length = 1)
            ),
            filler = Pattern(
              PatternItem(words("Senior"), tags("NNP")),
              PatternItem(words("Software"), tags("NNP")),
              PatternItem(words("Engineer"), tags("NNP"))
            ),
            postFiller = Pattern(
              PatternList(words("in"), tags("IN"), length = 1)
            ),
            slotName = SlotName("role")
          )
        )
      }
    }
  }
})

private val document = Document(Some("""
As a Senior Software Engineer in our Engineering team you will lead the technical
efforts to expand Cognitive Scale’s product capabilities.  You will work closely with
the CTO and the advanced technology team to create unique and differentiating
technology that solves challenging and “impossible” problems that matter to Cognitive
Scale and to the world.  Whether working directly with clients, contributing to
internal initiatives, giving technical talks, or supporting client delivery, you will
be on the cutting edge of the AI revolution. You can speak the language of any
department and are equally comfortable working with developers, user experience
designers, project managers, sales teams, and DevOps folks. In this role, you will
have the opportunity to put cognitive computin g in action to truly change the world!
Come join us on our journey....
    """))

private var document2 = Document(Some("""
The role is a self-organized Senior Software Engineer with solid Java and web service
development experience.  In this position, you are first and foremost a passionate and
talented developer that can work in a dynamic environment as a member of Agile Scrum
teams.  Your strong technical leadership, problem solving abilities, coding, testing
and debugging skills is just a start.  You must be dedicated to filling product
backlog and delivering production-ready code.  You must be willing to go beyond the
routine and prepared to do a little bit of everything.
"""))

private var document3 = Document((Some("""
We help people get jobs.
At Indeed, we know that mobile is big. 50% of all job searches on Indeed come from a
mobile device.This is why want to make applying to any job on your mobile or on your
tablet a breeze - no more pinching or zooming. As a Software Engineer - Client
Implementation, you will create mobile-friendly mirrors of clients’ career sites and
make sure our software knows how to send jobseekers’ applications correctly to the
original site.
""")))
