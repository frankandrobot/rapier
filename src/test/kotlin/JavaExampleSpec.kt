import com.frankandrobot.rapier.meta.*
import com.frankandrobot.rapier.rapier
import org.amshove.kluent.shouldNotEqual
import org.jetbrains.spek.api.Spek
import java.util.List


class JavaExampleSpec : Spek({

  val blankTemplate = BlankTemplate(
    "blue",
    listOf("playtime", "btVersion", "range", "watts") as java.util.List<String>
  )
  val ex1 = Example(
    blankTemplate,
    Document(
    """
        |The OontZ Angle 3 is Custom Designed and Engineered by Cambridge SoundWorks - An
        |industry trusted brand for over 35 years, to deliver the critical features
        |you're looking for in a portable Bluetooth speaker:
        |
        |• High quality, loud stereo sound
        |• Rich, deep bass
        |• IPX5 water resistant
        |• 2200 mAh battery for up to 12 hours of playtime at 2/3 volume on a full charge
        |• Built-In Mic for Hands-Free Speakerphone
        |• Ultra-lightweight
        |• Cool, stylish design
        |What makes it so much better?
        |EXCELLENT SOUND AND VOLUME
        |
        |• Two precision acoustic drivers provide very loud, distortion-free stereo
        | sound in exceptionally high quality
        |• Passive bass radiator delivers punchy bass for a perfect blend of crisp
        | highs, strong mids and deep lows; stand speaker on its side when playing bass
        |  heavy music to prevent it from moving around
        |• Power packed with loud sound to entertain and fill any room, outdoor
        | gathering, picnic and pool party
        |ULTRA PORTABLE
        |
        |• IPX5 Water Resistant pushes the boundaries of portability so you can bring
        | your OontZ Angle 3 to the beach, pool and in the shower
        |• Your go to compact speaker: weighs only 9 ounces and just over 5 inches long
        | to fit in your backpack, purse, suitcase or travel bag
        |• 2200 mAh battery with 12 hour battery life on a single charge for taking on the go
        |GREAT QUALITY
        |
        |• Lightweight sturdy design with no sharp edges or corners and fits comfortably
        |  in the palm of your hand
        |• Connect over Bluetooth in just a few seconds to pair with your device up to
        | 33 unobstructed feet away
      """.replace("|", "")
    ),
    FilledTemplate(
      hashMapOf(
        "playtime" to listOf("12 hours", "12 hour") as java.util.List<String>,
        "range" to listOf("33 unobstructed feet") as java.util.List<String>
      ) as java.util.Map<String, List<String>>
    )
  )

  val ex2 = Example(
    blankTemplate,
    Document(
    """
        |WATERPROOF IPX7:AOMAIS Sport II has designed and provided fully waterproof of
        |highest incursion protection degree of IPX7 (IPX7- Immersion up to 1 m for 30
        |minutes underwater),some competitors may offer an IPX5 rating only at splash
        |proof or light rain,not waterproof.To be dust proof,mud proof,shockproof,
        |rainproof,snowproof,free inflatable float accessory provided,which can float
        |your music in the swimming pool,shower,beach
        |
        |20W POWERFUL SOUND,STEREO PAIRING FUNCTION:Advanced digital sound,noise/wind
        |reduction technology,and unique loudspeaker cavity structure to deliver
        |premium acoustic sound,crystal clear balanced bass,if you have two AOMAIS SPORT
        |II Bluetooth speakers,connect them together to create left and right channels
        |to enjoy 40 Watts surround sound after pairing,perfect for home,parties,school,
        |Christmas gift,Thanksgiving day,Halloween,Black Friday
        |
        |DURABLE,RUGGED TOUGH DESIGN:Featuring a smooth rubber exterior that protects
        |  the speaker from scratches and impact,AOMIAS SPORT II is one of the toughest
        |   Bluetooth speakers,even car ran over it without damage,perfect for
        |   travel&hiking,camping,Boating,Kayaking,outdoor adventure
        |
        |RECHARGABLE BATTERY,HANDS FREE FUNCTION:Built-in high capacity rechargeable
        |  lithium polymer,can be replenished via the micro USB charging port. Answer
        |   phone calls using the hands free function. Bluetooth V4.0 Works with cell
        |    phones,amazon new echo dot,iphone,iPad, iPod, HTC,Samsung,Tablets,play audio
        |      from Laptops,PCs,mp3 player, 3.5mm audio mini jack allows simple wired
        |       connectivity.NFC (near-field communication) technology enables simple
        |       one-touch pairing with select devices
        |
        |WAHT YOU GET:AOMAIS SPORT II Bluetooth speaker,Micro USB charing cable,Aux in
        | cable,float speaker accessory,quick start guide,feedback card,our worry-free
        |   12-month warranty and friendly customer service,which make your purchase
        |    absolutely risk-free and you can enjoy testing out it's quality and
        |     durability!Official AOMAIS Sling Cover available on Amazon,you can search
        |     B01HRO56ZK
      """.replace("|", "")
    ),
    FilledTemplate(hashMapOf(
      "btVersion" to listOf("V4.0") as java.util.List<String>
    ) as java.util.Map<String, List<String>>)
  )

  val ex3 = Example(
    blankTemplate,
    Document(
    """
        |SUPERIOR SOUND QUALITY: Experience your music in full-bodied stereo and enjoy a
        |   High Definition stereo sound with a impressive volume. Up to 2X volume than
        |   most speaker in the market
        |COMPACT WIRELESS BLUETOOTH SPEAKER LOUD SOUND: Equiped with 10W total acoustic
        |  drivers produce a wide audio spectrum. The newly designed passive radiator
        |   could provide strong bass without distortion even at highest volume.
        |   Outstanding connection range of up to 66 feet.
        |UNIVERSAL CONNECT: With the advanced Bluetooth Technology, compatible with all
        | Bluetooth-enabled devices. Connected with Non-Bluetooth devices using included
        |   3.5mm audio cable/AUX-IN jack or with TF card, USB music player.
        |LONG PLAYTIME: Built-in 2000mA Rechargeable Battery for up to 6 hours of
        | playtime. Equiped with microphone for hands-free speaker phone calling.
        |  Built-in fine tunable FM Radio with LED display.
        |WHAT YOU GET: Standard package with 1 pack SARDINE bluetooth speaker, user
        |  manual, 3.5mm audio cable, USB charging cable, our worry-free 12-month
        |  warranty and friendly customer service, which make your purchase absolutely risk-free.
      """.replace("|","")
    ),
    FilledTemplate(hashMapOf(
      "watts" to listOf("10W") as java.util.List<String>,
      "playtime" to listOf("6 hours") as java.util.List<String>,
      "range" to listOf("66 feet") as java.util.List<String>
    ) as java.util.Map<String, List<String>>)
  )

  val ex4 = Example(
    blankTemplate,
    Document(
    """
        |Capacitive touch control: The DOSS Touch portable speaker makes it easy to
        |control the mood and energy of any party by giving you fingertip control of
        |the tracks you're playing, their volume and more
        |Wireless portable bluetooth speaker: Enjoy a High Definition stereo sound
        |with a impressive volume whether you're lounging around the house,or
        |partying,walking out,camping,hiking,biking with included waterproof travel pouch
        |Bluetooth 4.0 technology: Equipped with the advanced technology,compatible
        |with all Bluetooth compacity devices.Speaker automatically reconnects to the
        |last device used
        |Superior sound quality: Enjoy your music in 12W full-bodied stereo realized
        |through dual high-performance drivers and an unique enhanced bass
        |Long playtime: Built-in Li-Ion 2200 mAh rechargeable battery guarantees up to
        |12 hours playtime. Recharge in just 3-4 hours with included micro USB cable
      """.replace("|", "")
    ),
    FilledTemplate(hashMapOf(
      "btVersion" to listOf("Bluetooth 4.0") as java.util.List<String>,
      "watts" to listOf("12W") as java.util.List<String>,
      "playtime" to listOf("12 hours") as java.util.List<String>
    ) as java.util.Map<String, List<String>>)
  )

  val result = rapier(
    blankTemplate,
    Examples(listOf(ex1,ex2,ex3,ex4)),
    RapierParams(metricMinPositiveMatches = 0)
  )

  it("should work have rules for watts") {
    val finalResult = result.removeMostSpecific().toBaseRules()
    finalResult[SlotName("watts")].size shouldNotEqual 0
  }

  it("should have rules for range") {
    val finalResult = result.removeMostSpecific().toBaseRules()
    finalResult[SlotName("range")].size shouldNotEqual 0

    println(finalResult)
  }
})

