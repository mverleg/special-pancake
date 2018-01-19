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
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

class HelpScreen2(internal var game: SuperJumper): ScreenAdapter() {

    internal var guiCam: OrthographicCamera
    internal var nextBounds: Rectangle
    internal var touchPoint: Vector3
    internal var helpImage: Texture
    internal var helpRegion: TextureRegion

    init {

        guiCam = OrthographicCamera(320f, 480f)
        guiCam.position.set((320 / 2).toFloat(), (480 / 2).toFloat(), 0f)
        nextBounds = Rectangle((320 - 64).toFloat(), 0f, 64f, 64f)
        touchPoint = Vector3()
        helpImage = Assets.loadTexture("data/help2.png")
        helpRegion = TextureRegion(helpImage, 0, 0, 320, 480)
    }

    fun update() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (nextBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(Assets.clickSound!!)
                game.screen = HelpScreen3(game)
            }
        }
    }

    fun draw() {
        val gl = Gdx.gl
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()

        game.batcher.projectionMatrix = guiCam.combined
        game.batcher.disableBlending()
        game.batcher.begin()
        game.batcher.draw(helpRegion, 0f, 0f, 320f, 480f)
        game.batcher.end()

        game.batcher.enableBlending()
        game.batcher.begin()
        game.batcher.draw(Assets.arrow, 320f, 0f, -64f, 64f)
        game.batcher.end()

        gl.glDisable(GL20.GL_BLEND)
    }

    override fun render(delta: Float) {
        draw()
        update()
    }

    override fun hide() {
        helpImage.dispose()
    }
}
