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
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

class MainMenuScreen(internal var game: SuperJumper): ScreenAdapter() {
    internal var guiCam: OrthographicCamera
    internal var soundBounds: Rectangle
    internal var playBounds: Rectangle
    internal var highscoresBounds: Rectangle
    internal var touchPoint: Vector3

    init {

        guiCam = OrthographicCamera(320f, 480f)
        guiCam.position.set((320 / 2).toFloat(), (480 / 2).toFloat(), 0f)
        soundBounds = Rectangle(0f, 0f, 64f, 64f)
        playBounds = Rectangle((160 - 150).toFloat(), (200 + 18).toFloat(), 300f, 36f)
        highscoresBounds = Rectangle((160 - 150).toFloat(), (200 - 18).toFloat(), 300f, 36f)
        touchPoint = Vector3()
    }

    fun update() {

        val assets = game.assets!!

        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(assets.clickSound)
                game.screen = GameScreen(game)
                return
            }
            if (highscoresBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(assets.clickSound)
                game.screen = HighscoresScreen(game)
                return
            }
            if (soundBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(assets.clickSound)
                Settings.soundEnabled = !Settings.soundEnabled
                if (Settings.soundEnabled)
                    assets.music.play()
                else
                    assets.music.pause()
            }
        }
    }

    fun draw() {

        val assets = game.assets!!

        val gl = Gdx.gl
        gl.glClearColor(1f, 0f, 0f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()
        game.batcher.projectionMatrix = guiCam.combined

        game.batcher.disableBlending()
        game.batcher.begin()
        game.batcher.draw(assets.backgroundRegion, 0f, 0f, 320f, 480f)
        game.batcher.end()

        game.batcher.enableBlending()
        game.batcher.begin()
        game.batcher.draw(assets.logo, (160 - 274 / 2).toFloat(), (480 - 10 - 142).toFloat(), 274f, 142f)
        game.batcher.draw(assets.mainMenu, 10f, (200 - 110 / 2).toFloat(), 300f, 110f)
        game.batcher.draw(if (Settings.soundEnabled) assets.soundOn else assets.soundOff, 0f, 0f, 64f, 64f)
        game.batcher.end()
    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    override fun pause() {
        Settings.save()
    }
}
