package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.template.Slot


class Rule(val preFiller: Pattern,
           val filler: Pattern,
           val postFiller: Pattern,
           val slot: Slot)
