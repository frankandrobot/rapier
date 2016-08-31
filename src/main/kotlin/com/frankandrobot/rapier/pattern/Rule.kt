package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.template.Slot


class Rule(val preFiller: Pattern = Pattern(),
           val filler: Pattern = Pattern(),
           val postFiller: Pattern = Pattern(),
           val slot: Slot)
