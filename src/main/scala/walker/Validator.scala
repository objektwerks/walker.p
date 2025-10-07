package walker

object Validator:
  extension (walker: Walker)
    def isValid =
      walker.id >= 0 &&
      walker.name.length >= 2

  extension (session: Session)
    def isValid: Boolean =
      session.id >= 0 &&
      session.walkerId > 0 &&
      session.weight > 0 &&
      session.weightUnit.length == 2 &&
      session.distance > 0 &&
      session.distanceUnit.length == 2 &&
      session.hours >= 0 &&
      session.minutes > 0 &&
      session.calories >= 0 &&
      session.datetime > 0