/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.badlogicgames.superjumper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion

object Assets {
    var background: Texture? = null
    var backgroundRegion: TextureRegion? = null

    var items: Texture? = null
    var mainMenu: TextureRegion? = null
    var pauseMenu: TextureRegion? = null
    var ready: TextureRegion? = null
    var gameOver: TextureRegion? = null
    var highScoresRegion: TextureRegion? = null
    var logo: TextureRegion? = null
    var soundOn: TextureRegion? = null
    var soundOff: TextureRegion? = null
    var arrow: TextureRegion? = null
    var pause: TextureRegion? = null
    var spring: TextureRegion? = null
    var castle: TextureRegion? = null
    var coinAnim: Animation? = null
    var bobJump: Animation? = null
    var bobFall: Animation? = null
    var bobHit: TextureRegion? = null
    var squirrelFly: Animation? = null
    var platform: TextureRegion? = null
    var brakingPlatform: Animation? = null
    var font: BitmapFont? = null

    var music: Music? = null
    var jumpSound: Sound? = null
    var highJumpSound: Sound? = null
    var hitSound: Sound? = null
    var coinSound: Sound? = null
    var clickSound: Sound? = null

    fun loadTexture(file: String): Texture {
        return Texture(Gdx.files.internal(file))
    }

    fun load() {
        background = loadTexture("data/background.png")
        backgroundRegion = TextureRegion(background, 0, 0, 320, 480)

        items = loadTexture("data/items.png")
        mainMenu = TextureRegion(items, 0, 224, 300, 110)
        pauseMenu = TextureRegion(items, 224, 128, 192, 96)
        ready = TextureRegion(items, 320, 224, 192, 32)
        gameOver = TextureRegion(items, 352, 256, 160, 96)
        highScoresRegion = TextureRegion(Assets.items, 0, 257, 300, 110 / 3)
        logo = TextureRegion(items, 0, 352, 274, 142)
        soundOff = TextureRegion(items, 0, 0, 64, 64)
        soundOn = TextureRegion(items, 64, 0, 64, 64)
        arrow = TextureRegion(items, 0, 64, 64, 64)
        pause = TextureRegion(items, 64, 64, 64, 64)

        spring = TextureRegion(items, 128, 0, 32, 32)
        castle = TextureRegion(items, 128, 64, 64, 64)
        coinAnim = Animation(0.2f, TextureRegion(items, 128, 32, 32, 32), TextureRegion(items, 160, 32, 32, 32),
                TextureRegion(items, 192, 32, 32, 32), TextureRegion(items, 160, 32, 32, 32))
        bobJump = Animation(0.2f, TextureRegion(items, 0, 128, 32, 32), TextureRegion(items, 32, 128, 32, 32))
        bobFall = Animation(0.2f, TextureRegion(items, 64, 128, 32, 32), TextureRegion(items, 96, 128, 32, 32))
        bobHit = TextureRegion(items, 128, 128, 32, 32)
        squirrelFly = Animation(0.2f, TextureRegion(items, 0, 160, 32, 32), TextureRegion(items, 32, 160, 32, 32))
        platform = TextureRegion(items, 64, 160, 64, 16)
        brakingPlatform = Animation(0.2f, TextureRegion(items, 64, 160, 64, 16), TextureRegion(items, 64, 176, 64, 16),
                TextureRegion(items, 64, 192, 64, 16), TextureRegion(items, 64, 208, 64, 16))

        font = BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false)

        music = Gdx.audio.newMusic(Gdx.files.internal("data/music.mp3"))
        val music = music!!
        music.isLooping = true
        music.volume = 0.5f
        if (Settings.soundEnabled) music.play()
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("data/jump.wav"))
        highJumpSound = Gdx.audio.newSound(Gdx.files.internal("data/highjump.wav"))
        hitSound = Gdx.audio.newSound(Gdx.files.internal("data/hit.wav"))
        coinSound = Gdx.audio.newSound(Gdx.files.internal("data/coin.wav"))
        clickSound = Gdx.audio.newSound(Gdx.files.internal("data/click.wav"))
    }

    fun playSound(sound: Sound) {
        if (Settings.soundEnabled) sound.play(1f)
    }
}
