package com.ghstudios.android.data.classes

/*
 * Class for Quest
 */
class Quest {
    companion object {
        const val QUEST_GOAL_HUNT = 0
        const val QUEST_GOAL_SLAY = 1
        const val QUEST_GOAL_CAPTURE = 2
        const val QUEST_GOAL_DELIVER = 3
        const val QUEST_GOAL_HUNTATHON = 4
        const val QUEST_GOAL_MARATHON = 5
    }

    /* Getters and Setters */
    var id: Long = -1
    var name = ""
    var jpnName = ""

    // Clear condition
    var goal: String? = ""

    // Port or village
    var hub: String? = ""

    // 0=Normal,1=Key,2=Urgent
    var type: Int = 0

    var stars: String? = ""
    var location: Location? = null

    // 0 = Hunter / 1 = Cat
    var hunterType: Int = 0

    //private String locationTime;// Day or Night
    /*public String getLocationTime() {
		return locationTime;
	}*/

    /*public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}*/

    var timeLimit: Int = -1
    var fee: Int = -1

    // Quest reward in zenny
    var reward: Int = -1

    // Quest reward in Hunting rank points
    var hrp: Int = -1

    // Subquest Clear condition
    var subGoal: String? = ""

    // Subquest reward in zenny
    var subReward: Int = -1

    // Subquest reward in Hunting rank points
    var subHrp: Int = -1

    // Quest description
    var flavor: String? = ""

    //Quest goal -> one of the following constants: (todo: document)
    var goalType: Int = 0

    var rank: String? = null

    var metadata: Int = 0

    // todo: we'll need a better way to do this that allows localization
    val typeText: String
        get() {
            val keyText: String
            if (type == 0)
                keyText = ""
            else if (type == 1)
                keyText = "Key"
            else
                keyText = "Urgent"
            return keyText
        }


    /**
     * Resolves to true if the quest contains a gathering item
     */
    val hasGatheringItem get() = metadata and 1 > 0

    fun HasAcademyPointRequirement(): Boolean {
        return metadata and 2 > 0
    }

    override fun toString(): String {
        return this.name
    }
}