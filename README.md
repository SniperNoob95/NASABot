# NASABot v11.1.0
A Discord bot that implements NASA and other space-related APIs to provide fun, interesting, and informative artifacts.
This includes daily APOD postings, daily Moonphase postings, ISS location, NASA image database search, and more! New features are always 
being explored and added!

## Credit/Attribution
**All data presented is owned by NASA and/or its affiliates which provide this data. For more information,
please refer to the open source code in this repository to check the hosts of the various APIs used to retrieve data.**

\
**Upvote us on [Top.gg](https://top.gg/bot/748775876077813881)!**

#### [Discord Invite Link](https://discord.com/api/oauth2/authorize?client_id=748775876077813881&permissions=2214710336&scope=applications.commands%20bot)

**Contact `snipernoob` on Discord if needed.**

## Command Usage
#### /setpostchannel
	/setpostchannel <#channel mention>
    Ex: /setpostchannel #lounge
Sets the Post Channel for the server to receive automated APOD postings each day.

#### /removepostchannel
Removes the Post Channel set for the server.

#### /getpostchannel
Returns the Post Channel set for the server.

#### /setposttime
    /setposttime <option>
    Ex: /setposttime 1
    Options are as follows:
        0 (default): 16:00 UTC
        1: 6:00 UTC
        2: 11:00 UTC
        3: 21:00 UTC
Sets the Post Time for the server to receive automated APOD postings each day. By default, all servers use option 0 until overridden.

#### /getposttime
Returns the Post Time set for the server.

#### /setmoonphasechannel
	/setmoonphasechannel <#channel mention>
    Ex: /setmoonphasechannel #lounge
Sets the Moonphase Channel for the server to receive automated Moonphase postings each day.

#### /removemoonphasechannel
Removes the Moonphase Channel set for the server.

#### /getmoonphasechannel
Returns the Moonphase Channel set for the server.

#### /setmoonphasetime
    /setmoonphasetime <option>
    Ex: /setmoonphasetime 1
    Options are as follows:
        0 (default): 16:00 UTC
        1: 6:00 UTC
        2: 11:00 UTC
        3: 21:00 UTC
Sets the Moonphase Time for the server to receive automated Moonphase postings each day. By default, all servers use option 0 until overridden.

#### /getmoonphasetime
Returns the Moonphase Time set for the server.

#### /apod
    /apod [date (YYYY-MM-DD)]
    Ex: /apod 2020-09-05
Returns the Astronomy Picture of the Day for the previous day, or the given date if provided.

#### /marsweather
Explore Mars weather from the InSight Mars Lander!

#### /moonphase
Returns the current moon phase and time until next new moon.

#### /iss
Displays the current location of the International Space Station.

#### /image
    /image <search term> [page]
    Ex: /image black hole
Returns images from the NASA image database that match the given search term.

#### /info
Returns information about the bot.

#### /help
Sends the necessary information for using the bot.
