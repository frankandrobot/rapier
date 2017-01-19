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
        roleDocument,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(
            wordTokens("Senior", "Software", "Engineer")
          )
        ))
      )
      val example2 = Example(
        blankTemplate,
        roleDocument2,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(
            wordTokens("Senior", "Software", "Engineer")
          )
        ))
      )
      val examples = Examples(listOf(example, example2))
      val params = RapierParams(
        Random = Random(20),
        compressionFails = 3,
        metricMinPositiveMatches = 1,
        compressionPriorityQueueSize = 7,
        ruleSizeWeight = 0.01
      )

      it("should find simplest rule") {
        val result = rapier(blankTemplate, examples = examples, params = params)
        result[0].learnedRules.map(::toBaseRule) shouldEqual listOf(
          BaseRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternItem(words("Senior"), tags("NNP")),
              PatternItem(words("Software"), tags("NNP")),
              PatternItem(words("Engineer"), tags("NNP"))
            ),
            postFiller = Pattern(),
            slotName = SlotName("role")
          )
        )
      }
    }


    describe("two examples with similar fillers") {

      val blankTemplate = BlankTemplate(
        name = "test",
        slots = slotNames("exp")
      )
      val example = Example(
        blankTemplate,
        yearsDocument1,
        FilledTemplate(slots(
          SlotName("exp") to slotFillers(wordTokens("5", "+", "years", "experience"))
        ))
      )
      val example2 = Example(
        blankTemplate,
        yearsDocument2,
        FilledTemplate(slots(
          SlotName("exp") to slotFillers(wordTokens("6", "+", "years"))
        ))
      )
      val examples = Examples(listOf(example, example2))
      val params = RapierParams(
        Random = Random(20),
        compressionFails = 7,
        metricMinPositiveMatches = 1,
        compressionPriorityQueueSize = 5
        //ruleSizeWeight = 0.0
      )

      it("should find a rule") {
        val result = rapier(blankTemplate, examples = examples, params = params)
        result[0].learnedRules.map(::toBaseRule) shouldEqual listOf(
          BaseRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternItem(posTagConstraints = tags("CD")),
              PatternItem(words("+"), tags("SYM")),
              PatternItem(words("years"), tags("NNS")),
              PatternList(words("experience"),tags("NN"), length = 1)
            ),
            postFiller = Pattern(
              PatternItem(posTagConstraints = tags("IN", "VBG"))
            ),
            slotName=SlotName(name="exp")
          )
        )
      }
    }


    describe("non-intuitive filledTemplate examples") {

      val blankTemplate = BlankTemplate(
        name = "test",
        slots = slotNames("role")
      )
      val example = Example(
        blankTemplate,
        roleDocument,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(
            wordTokens("Senior", "Software", "Engineer"),
            wordTokens("Software", "Engineer")
          )
        ))
      )
      val example2 = Example(
        blankTemplate,
        roleDocument2,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(
            wordTokens("Senior", "Software", "Engineer"),
            wordTokens("Software", "Engineer")
          )
        ))
      )
      val example3 = Example(
        blankTemplate,
        roleDocument3,
        FilledTemplate(slots(
          SlotName("role") to slotFillers(wordTokens("Software", "Engineer"))
        ))
      )
      val examples = Examples(listOf(example, example2, example3))
      val params = RapierParams(
        Random = Random(20),
        compressionFails = 3,
        metricMinPositiveMatches = 1,
        compressionPriorityQueueSize = 7,
        ruleSizeWeight = 0.01
      )

      it("should find a simple rule") {
        val result = rapier(blankTemplate, examples = examples, params = params)
        result[0].learnedRules.map(::toBaseRule) shouldEqual listOf(
          BaseRule(
            preFiller = Pattern(),
            filler = Pattern(
              PatternList(words("Senior"), tags("NNP"), length = 1),
              PatternItem(words("Software"), tags("NNP")),
              PatternItem(words("Engineer"), tags("NNP"))
            ),
            postFiller = Pattern(),
            slotName = SlotName("role")
          )
        )
      }
    }
  }
})

private val roleDocument = Document("""
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
    """)

private var roleDocument2 = Document("""
The role is a self-organized Senior Software Engineer with solid Java and web service
development experience.  In this position, you are first and foremost a passionate and
talented developer that can work in a dynamic environment as a member of Agile Scrum
teams.  Your strong technical leadership, problem solving abilities, coding, testing
and debugging skills is just a start.  You must be dedicated to filling product
backlog and delivering production-ready code.  You must be willing to go beyond the
routine and prepared to do a little bit of everything.
""")

private var roleDocument3 = Document("""
We help people get jobs.
At Indeed, we know that mobile is big. 50% of all job searches on Indeed come from a
mobile device. This is why want to make applying to any job on your mobile or on your
tablet a breeze - no more pinching or zooming. As a Software Engineer - Client
Implementation, you will create mobile-friendly mirrors of clients’ career sites and
make sure our software knows how to send jobseekers’ applications correctly to the
original site.
""")

private var yearsDocument1 = Document("""
Requirements:

* 5+ years experience in Object Oriented Design and Programming or Functional Programing
* Hands-on expertise with Java, JavaScript, Python, or Scala
* Experience working on large software systems, particularly distributed systems and
microservices architectures
* Experience architecting large software systems and enterprise integrations
* Solid grasp and fluent in common design patterns
* Experience working with large transactional data streams and combining transactional
data with web, social and mobile data streams
* Demonstrated ability to effectively establish and maintain working relationships with
all levels of the organization
* Keen business judgment, focus, and ability to see the "big picture" and prioritize
* Excellent communication and presentation skills with the ability to present and
translate complex information to both internal and external teams in relevant business terms
""")

private var yearsDocument2 = Document("""
We are looking for someone with:
* Bachelor's degree in Computer Science or similar desired
* 6+ years developing high-quality software
* Experience with .NET, Node.js, Java
* Strong knowledge of services (mostly RESTful)
* AWS experience
* Database knowledge; SQLServer and MongoDB
* Strong interest in learning and expanding your personal toolbox of skills
* High emotional intelligence
""")
