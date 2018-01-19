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

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogicgames.superjumper.World.WorldListener

class GameScreen(internal var game: SuperJumper): ScreenAdapter() {

    internal var state: Int = 0
    internal var guiCam: OrthographicCamera
    internal var touchPoint: Vector3
    internal var world: World
    internal var worldListener: WorldListener
    internal var renderer: WorldRenderer
    internal var pauseBounds: Rectangle
    internal var resumeBounds: Rectangle
    internal var quitBounds: Rectangle
    internal var lastScore: Int = 0
    internal var scoreString: String

    internal var glyphLayout = GlyphLayout()

    init {

        state = GAME_READY
        guiCam = OrthographicCamera(320f, 480f)
        guiCam.position.set((320 / 2).toFloat(), (480 / 2).toFloat(), 0f)
        touchPoint = Vector3()
        worldListener = object: WorldListener {
            override fun jump() {
                Assets.playSound(Assets.jumpSound!!)
            }

            override fun highJump() {
                Assets.playSound(Assets.highJumpSound!!)
            }

            override fun hit() {
                Assets.playSound(Assets.hitSound!!)
            }

            override fun coin() {
                Assets.playSound(Assets.coinSound!!)
            }
        }
        world = World(worldListener)
        renderer = WorldRenderer(game.batcher, world)
        pauseBounds = Rectangle((320 - 64).toFloat(), (480 - 64).toFloat(), 64f, 64f)
        resumeBounds = Rectangle((160 - 96).toFloat(), 240f, 192f, 36f)
        quitBounds = Rectangle((160 - 96).toFloat(), (240 - 36).toFloat(), 192f, 36f)
        lastScore = 0
        scoreString = "SCORE: 0"
    }

    fun update(deltaTime: Float) {
        var deltaTime = deltaTime
        if (deltaTime > 0.1f) deltaTime = 0.1f

        when (state) {
            GAME_READY -> updateReady()
            GAME_RUNNING -> updateRunning(deltaTime)
            GAME_PAUSED -> updatePaused()
            GAME_LEVEL_END -> updateLevelEnd()
            GAME_OVER -> updateGameOver()
        }
    }

    private fun updateReady() {
        if (Gdx.input.justTouched()) {
            state = GAME_RUNNING
        }
    }

    private fun updateRunning(deltaTime: Float) {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound!!)
                state = GAME_PAUSED
                return
            }
        }

        val appType = Gdx.app.type

        // should work also with Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)
        if (appType == ApplicationType.Android || appType == ApplicationType.iOS) {
            world.update(deltaTime, Gdx.input.accelerometerX)
        } else {
            var accel = 0f
            if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) accel = 5f
            if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) accel = -5f
            world.update(deltaTime, accel)
        }
        if (world.score != lastScore) {
            lastScore = world.score
            scoreString = "SCORE: " + lastScore
        }
        if (world.state == World.WORLD_STATE_NEXT_LEVEL) {
            game.screen = WinScreen(game)
        }
        if (world.state == World.WORLD_STATE_GAME_OVER) {
            state = GAME_OVER
            if (lastScore >= Settings.highscores[4])
                scoreString = "NEW HIGHSCORE: " + lastScore
            else
                scoreString = "SCORE: " + lastScore
            Settings.addScore(lastScore)
            Settings.save()
        }
    }

    private fun updatePaused() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (resumeBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound!!)
                state = GAME_RUNNING
                return
            }

            if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound!!)
                game.screen = MainMenuScreen(game)
                return
            }
        }
    }

    private fun updateLevelEnd() {
        if (Gdx.input.justTouched()) {
            world = World(worldListener)
            renderer = WorldRenderer(game.batcher, world)
            world.score = lastScore
            state = GAME_READY
        }
    }

    private fun updateGameOver() {
        if (Gdx.input.justTouched()) {
            game.screen = MainMenuScreen(game)
        }
    }

    fun draw() {
        val gl = Gdx.gl
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.render()

        guiCam.update()
        game.batcher.projectionMatrix = guiCam.combined
        game.batcher.enableBlending()
        game.batcher.begin()
        when (state) {
            GAME_READY -> presentReady()
            GAME_RUNNING -> presentRunning()
            GAME_PAUSED -> presentPaused()
            GAME_LEVEL_END -> presentLevelEnd()
            GAME_OVER -> presentGameOver()
        }
        game.batcher.end()
    }

    private fun presentReady() {
        game.batcher.draw(Assets.ready, (160 - 192 / 2).toFloat(), (240 - 32 / 2).toFloat(), 192f, 32f)
    }

    private fun presentRunning() {
        game.batcher.draw(Assets.pause, (320 - 64).toFloat(), (480 - 64).toFloat(), 64f, 64f)
        Assets.font!!.draw(game.batcher, scoreString, 16f, (480 - 20).toFloat())
    }

    private fun presentPaused() {
        game.batcher.draw(Assets.pauseMenu, (160 - 192 / 2).toFloat(), (240 - 96 / 2).toFloat(), 192f, 96f)
        Assets.font!!.draw(game.batcher, scoreString, 16f, (480 - 20).toFloat())
    }

    private fun presentLevelEnd() {
        glyphLayout.setText(Assets.font, "the princess is ...")
        Assets.font!!.draw(game.batcher, glyphLayout, 160 - glyphLayout.width / 2, (480 - 40).toFloat())
        glyphLayout.setText(Assets.font, "in another castle!")
        Assets.font!!.draw(game.batcher, glyphLayout, 160 - glyphLayout.width / 2, 40f)
    }

    private fun presentGameOver() {
        game.batcher.draw(Assets.gameOver, (160 - 160 / 2).toFloat(), (240 - 96 / 2).toFloat(), 160f, 96f)
        glyphLayout.setText(Assets.font, scoreString)
        Assets.font!!.draw(game.batcher, scoreString, 160 - glyphLayout.width / 2, (480 - 20).toFloat())
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    override fun pause() {
        if (state == GAME_RUNNING) state = GAME_PAUSED
    }

    companion object {
        internal val GAME_READY = 0
        internal val GAME_RUNNING = 1
        internal val GAME_PAUSED = 2
        internal val GAME_LEVEL_END = 3
        internal val GAME_OVER = 4
    }
}