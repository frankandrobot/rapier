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

## Filled Template

Given a *sample document* and a list of *slots*, we find values (called
*slot-fillers*) from the document for each slot. The slots and corresponding
values form a list of key/value pairs called a **filled
template**.

Note there can be more than one value per slot.


Example:

```
//Sample Document:

Software Developer
Position available for Software Developer experienced in C++ and Java.
Salary is 40k.
```

```javascript
filledTemplate = [
  {title: Software Developer},
  {salary: 40k},
  {languages: C++},
  {languages: Java}
]
```

## Slot-Filler
the string to be extracted in the sample document for a given slot.

## Pre-Filler Pattern
a pattern that matches text immediately to the left of the slot-filler

## Post-Filler Pattern
a pattern that matches text immediately to the right of the slot-filler.

## Rule
Consists of 3 patterns:

1.  a *pre-filler pattern* that must match the text immediately preceding
    the slot-filler.
2.  *slot-filler pattern* that must match the actual slot-filler.
3.  *a post-filler pattern* that must match the text
    immediately following the filler.

Each rule also contains information about what template and slot they
apply to.

## Definition
is specific to a slot. Consists of one or more rules.

# Compression

Rapier begins with an initial definition and then compresses it by replacing
rules with more general rules. Since a slot's rules are independent of other
slot rules, the system actually creates the most specific definition and then
compacts it separately for each slot in the template.

# Main outer loop

```
fun outerLoop(CompressLim : Int) {
  For each slot, S in the template being learned
    SlotRules = most specific rules for S from example documents
Failures = 0
while Failures < CompressLim
BestNewRule = FindNewRule(SlotRules, Examples)
if BestNewRule is acceptable
add BestNewRule to SlotRules
remove empirically subsumed rules from SlotRules
else
Add 1 to Failures
```
