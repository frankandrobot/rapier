# Terminology
## Constraint
Disjunctive lists of one or more words, POS tags, or semantic classes.

Examples:

```javascript
wordConstraint = ['foo', 'bar']
posTagConstraint = ['jj']      // "jj" is a POS tag for an adjective
semanticConstraint = ['price']
```

The word `blue` in "the blue car" satisfies the above POS tag constraint.

## Pattern Element

Consists of one or more constraints. A pattern element has at most one
constraint of each type. A word or symbol is said to *match the pattern* when it
satisfies all of the pattern constraints.

Example:

```javascript
pattern1 = {
  wordConstraint: ['undisclosed']
  posTagConstraint: ['jj']
}
```

`pattern1` matches the word `undisclosed` in "sold for an undisclosed amount".

## Pattern Item

A pattern element that matches exactly one word or symbol.

## Pattern List

A pattern element that specifies a maximum length N and matches 0 to N words or
symbols.

## Pattern

A sequence of 0 or more pattern elements. We use patterns for information
extraction.

Example 1:

```javascript
patternItem = {
  wordConstraint: ['foo']
}

patternList = {
  maxLength: 2 // any sequence of 2 words or symbols
}

pattern = [
  patternItem,
  patternList
]
```

Examples of matching phrases:

-  `foo`
-  `foo hello`
-  `foo hello world`
-  `foo bar.`

Example 2:

```javascript
patternList = {
  wordConstraint: ['woman'],
  posTagConstraint: ['nnp']  // proper noun
  maxLength: 1               // note that a patternList of length 1 != patternItem
}

patternItem = {
  wordConstraint: ['girl', 'boy']
}

pattern = [
  patternList,
  patternItem
]
```

Examples of matching phrases:

-  `girl`
-  `boy`
-  `girl woman`

## Template

A list of **slots**, which are the names of the patterns to extract from a
document.

Example:

```javascript
//template to extract information from a job listing
template = [
  'title',
  'salary',
  'programming languages',
  'state'
]

```
## Filled Template

A template and the actual values from a *sample document*.

Example:

```javascript
sampleDocument = `
Software Developer
Position available for Software Developer experienced in C++ and Java.
Salary is 40k.
`
filledTemplate = {
  filledSlots: [
    {slot: 'title', slotFiller: 'Software Developer'},
    {slot: 'salary', slotFiller: '40k'},
    {slot: 'languages', slotFiller: 'C++'},
    {slot: 'languages', slotFiller: 'Java'},
    {slot: 'state', slotFiller: undefined}
  ],
  document: sampleDocument
}
```

Notes:

1.  The values found in the document are called **slot-fillers**.
2.  The slots and the slot-fillers form a list of key/value pairs.
3.  There can be more than one slot-filler per slot.
4.  Slots can be empty.

## Examples

A template, a set of documents and the corresponding filled templates.

Example:

```javascript
template = ['profession']
document1 = 'John is a teacher.'
document2 = 'Sam is a lawyer. Debbie is a doctor.'
filledTemplate1 = {
  filledSlots: [
    {slot: 'profession', slotFiller: 'teacher'}
  ],
  document: document1
}
filledTemplate2 = {
  filledSlots: [
    {slot: 'profession', slotFiller: 'lawyer'},
    {slot: 'profession', slotFiller: 'doctor'}
  ],
  document: document2
}

examples = {
  template,
  documents: [document1, document2],
  filledTemplates = [filledTemplate1, filledTemplate2]
}
```

Note that this structure is for learning purposes only. In actuality, it makes
sense to partition the examples by slots.

## Slot-Filler

The string to be extracted in the sample document for a given slot.

## Pre-Filler Pattern

A pattern that matches text immediately to the left of the slot-filler

## Post-Filler Pattern

A pattern that matches text immediately to the right of the slot-filler.

## Rule

Consists of 3 patterns:

1.  a *pre-filler pattern* that must match the text immediately preceding
    the slot-filler.
2.  *slot-filler pattern* that must match the actual slot-filler.
3.  *a post-filler pattern* that must match the text
    immediately following the filler.

Each rule also contains information about what filled template and slot they
apply to.

## Definition

is specific to a slot. Consists of one or more rules.

## Compression

Systems that use compression begin by creating an initial set of highly specific
rules, typically one for each example. At each iteration a more general rule is
constructed, which replaces the rules it subsumes, thus compressing the rule
set. At each iteration, all positive examples are under consideration to some
extent, and the metric for evaluating new rules is biased toward greater
compression of the rule set. Rule learning ends when no new rules to compress
the rule set are found.

Rapier begins with an initial definition and then compresses it by replacing
rules with more general rules. Since a slot's rules are independent of other
slot rules, the system actually creates the most specific definition and then
compacts it separately for each slot in the template.

## Empircal Subsumption

If a clause A covers all of the examples covered by clause B along with one or
more additional examples, then A empirically subsumes B.

# Overview
## Main loop

```javascript
/**
 * @param {Example} examples
 * @param {Int} compressLim
 */
function mainLoop(examples, compressLim) {

  for(slot in examples.template) {

    const slotRules = initialRules(slot, examples.filledTemplates)

    let failures = 0

    while(failures < compressLim) {

      const bestNewRule = findNewRule(slotRules, examples)

      if (bestNewRule is acceptable) { // TODO
        slotRules.add(bestNewRule)
        //remove empirically subsumed rules from SlotRules TODO
      }
      else
        failures++
    }
  }
}
```

## initialRules

```javascript
/**
 * @param {Slot} slot
 * @param {Array[filledTemplates]} filledTemplates
 */
fun initialRules(slot, filledTemplates) {

  return filledTemplates.flatMap(filledTemplate => {

    const filledSlots = filledTemplate.filledSlots
    const document = filledTemplate.document
    const matchingFilledSlots = filledSlots.filter(filledSlot => filledSlot.slot === slot)

    return matchingFilledSlots.map(filledSlot => {

      // construct a rule each filledSlot, using everything in the document
      // preceding the slotFiller as the pre-filler, everything following the
      // slotFiller as the post-filler.

      // Since the rules are to be maximally specific, the constructed patterns
      // contain one pattern item for each token of the document.

      // There are no semantic contraints, only word and POS tag constraints.
    })
  })
}
```
## fileNewRule

```javascript
/**
 * @param {SlotRules} slotRules for a given slot
 * @param {Examples} set of examples
 * @param {Int} k - size of priority queue
 * @param {Int} m - number of pairs to select from SlotRules
 */
function findNewRule(slotRules, examples, k, m) {
  const ruleList = new PriorityQueue(k)
  const rulePairs = randomSelect(slotRules)
  const generalizations = findFillerGeneralizations(rulePairs)
  for(pattern in generalizations) {

    // create a rule NewRule with filler P and empty pre- and post-fillers
    //evaluate NewRule and add NewRule to RuleList
  }
  let n = 0
  do {
    ++n
    for each rule, CurRule, in RuleList
      NewRuleList = SpecializePreFiller (CurRule, n)
      evaluate each rule in NewRuleList and add it to RuleList
    for each rule, CurRule, in RuleList
      NewRuleList = SpecializePostFiller (CurRule, n)
      evaluate each rule in NewRuleList and add it to RuleList
  }
  until best rule in RuleList produces only valid fillers or
  the value of the best rule in RuleList has failed to
  improve over the last LimNoImprovements iterations
```
