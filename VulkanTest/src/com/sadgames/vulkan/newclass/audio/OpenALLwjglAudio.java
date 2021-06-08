/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.sadgames.vulkan.newclass.audio;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import com.sadgames.vulkan.newclass.GdxMathUtils;
import com.sadgames.vulkan.newclass.IntArray;
import com.sadgames.vulkan.newclass.IntMap;
import com.sadgames.vulkan.newclass.LongMap;
import com.sadgames.vulkan.newclass.ObjectMap;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.ALC_FALSE;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.system.MemoryUtil.NULL;

/** @author Nathan Sweet */
public class OpenALLwjglAudio {
	private final int deviceBufferSize;
	private final int deviceBufferCount;
	private IntArray idleSources, allSources;
	private LongMap<Integer> soundIdToSource;
	private IntMap<Long> sourceToSoundId;
	private long nextSoundId = 0;
	private ObjectMap<String, Class<? extends OpenALSound>> extensionToSoundClass = new ObjectMap();
	///private ObjectMap<String, Class<? extends OpenALMusic>> extensionToMusicClass = new ObjectMap();
	private OpenALSound[] recentSounds;
	private int mostRecetSound = -1;

	///Array<OpenALMusic> music = new Array(false, 1, OpenALMusic.class);
	boolean noDevice = false;

	public OpenALLwjglAudio () {
		this(16, 9, 512);
	}

	public OpenALLwjglAudio (int simultaneousSources, int deviceBufferCount, int deviceBufferSize) {
		this.deviceBufferSize = deviceBufferSize;
		this.deviceBufferCount = deviceBufferCount;

		///registerSound("ogg", Ogg.Sound.class);
		///registerMusic("ogg", Ogg.Music.class);
		///registerSound("wav", Wav.Sound.class);
		///registerMusic("wav", Wav.Music.class);
		registerSound("mp3", Mp3.Sound.class);
		///registerMusic("mp3", Mp3.Music.class);

		try {
			//Start by acquiring the default device
			long device = ALC10.alcOpenDevice((ByteBuffer)null);
			if (device == NULL)
				throw new Exception("Failed to open device");

			//Create a handle for the device capabilities, as well.
			ALCCapabilities deviceCaps = ALC.createCapabilities(device);
			// Create context (often already present, but here, necessary)
			IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

			// Note the manner in which parameters are provided to OpenAL...
			contextAttribList.put(ALC_REFRESH);
			contextAttribList.put(60);

			contextAttribList.put(ALC_SYNC);
			contextAttribList.put(ALC_FALSE);

			// Don't worry about this for now; deals with effects count
			contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
			contextAttribList.put(2);

			contextAttribList.put(0);
			contextAttribList.flip();

			//create the context with the provided attributes
			long newContext = ALC10.alcCreateContext(device, contextAttribList);

			if(!ALC10.alcMakeContextCurrent(newContext)) {
				throw new Exception("Failed to make context current");
			}

			AL.createCapabilities(deviceCaps);
		} catch (Exception ex) {
			noDevice = true;
			ex.printStackTrace();
			return;
		}

		allSources = new IntArray(false, simultaneousSources);
		for (int i = 0; i < simultaneousSources; i++) {
			int sourceID = alGenSources();
			if (alGetError() != AL_NO_ERROR) break;
			allSources.add(sourceID);
		}
		idleSources = new IntArray(allSources);
		soundIdToSource = new LongMap<Integer>();
		sourceToSoundId = new IntMap<Long>();

		FloatBuffer orientation = BufferUtils.createFloatBuffer(6)
			.put(new float[] {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).flip();
		alListenerfv(AL_ORIENTATION, orientation);
		FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f}).flip();
		alListenerfv(AL_VELOCITY, velocity);
		FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f}).flip();
		alListenerfv(AL_POSITION, position);

		recentSounds = new OpenALSound[simultaneousSources];
	}

	public void registerSound (String extension, Class<? extends OpenALSound> soundClass) {
		if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
		if (soundClass == null) throw new IllegalArgumentException("soundClass cannot be null.");
		extensionToSoundClass.put(extension, soundClass);
	}

	/*public void registerMusic (String extension, Class<? extends OpenALMusic> musicClass) {
		if (extension == null) throw new IllegalArgumentException("extension cannot be null.");
		if (musicClass == null) throw new IllegalArgumentException("musicClass cannot be null.");
		extensionToMusicClass.put(extension, musicClass);
	}*/

	public OpenALSound newSound (InputStream file) {
		if (file == null) throw new IllegalArgumentException("file cannot be null.");
		Class<? extends OpenALSound> soundClass = Mp3.Sound.class/*extensionToSoundClass.get(file.extension().toLowerCase())*/;
		if (soundClass == null) throw new RuntimeException("Unknown file extension for sound: ");
		try {
			return soundClass.getConstructor(new Class[] {OpenALLwjglAudio.class, /*FileHandle*/InputStream.class}).newInstance(this, file);
		} catch (Exception ex) {
			throw new RuntimeException("Error creating sound " + soundClass.getName(), ex);
		}
	}

	/*public OpenALMusic newMusic (FileHandle file) {
		if (file == null) throw new IllegalArgumentException("file cannot be null.");
		Class<? extends OpenALMusic> musicClass = extensionToMusicClass.get(file.extension().toLowerCase());
		if (musicClass == null) throw new RuntimeException("Unknown file extension for music: " + file);
		try {
			return musicClass.getConstructor(new Class[] {OpenALLwjglAudio.class, FileHandle.class}).newInstance(this, file);
		} catch (Exception ex) {
			throw new RuntimeException("Error creating music " + musicClass.getName() + " for file: " + file, ex);
		}
	}*/

	int obtainSource (boolean isMusic) {
		if (noDevice) return 0;
		for (int i = 0, n = idleSources.size; i < n; i++) {
			int sourceId = idleSources.get(i);
			int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
			if (state != AL_PLAYING && state != AL_PAUSED) {
				if (isMusic) {
					idleSources.removeIndex(i);
				} else {
					Long oldSoundId = sourceToSoundId.remove(sourceId);
					if (oldSoundId != null) soundIdToSource.remove(oldSoundId);

					long soundId = nextSoundId++;
					sourceToSoundId.put(sourceId, soundId);
					soundIdToSource.put(soundId, sourceId);
				}
				alSourceStop(sourceId);
				alSourcei(sourceId, AL_BUFFER, 0);
				AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
				AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
				AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 1f);
				return sourceId;
			}
		}
		return -1;
	}

	void freeSource (int sourceID) {
		if (noDevice) return;
		alSourceStop(sourceID);
		alSourcei(sourceID, AL_BUFFER, 0);
		Long soundId = sourceToSoundId.remove(sourceID);
		if (soundId != null) soundIdToSource.remove(soundId);
		idleSources.add(sourceID);
	}

	void freeBuffer (int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size; i < n; i++) {
			int sourceID = idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				Long soundId = sourceToSoundId.remove(sourceID);
				if (soundId != null) soundIdToSource.remove(soundId);
				alSourceStop(sourceID);
				alSourcei(sourceID, AL_BUFFER, 0);
			}
		}
	}

	void stopSourcesWithBuffer (int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size; i < n; i++) {
			int sourceID = idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				Long soundId = sourceToSoundId.remove(sourceID);
				if (soundId != null) soundIdToSource.remove(soundId);
				alSourceStop(sourceID);
			}
		}
	}

	void pauseSourcesWithBuffer (int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size; i < n; i++) {
			int sourceID = idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) alSourcePause(sourceID);
		}
	}

	void resumeSourcesWithBuffer (int bufferID) {
		if (noDevice) return;
		for (int i = 0, n = idleSources.size; i < n; i++) {
			int sourceID = idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				if (alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PAUSED) alSourcePlay(sourceID);
			}
		}
	}

	public void update () {
		/*if (noDevice) return;
		for (int i = 0; i < music.size; i++)
			music.items[i].update();*/
	}

	public long getSoundId (int sourceId) {
		Long soundId = sourceToSoundId.get(sourceId);
		return soundId != null ? soundId : -1;
	}

	public int getSourceId (long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		return sourceId != null ? sourceId : -1;
	}

	public void stopSound (long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) alSourceStop(sourceId);
	}

	public void pauseSound (long soundId) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) alSourcePause(sourceId);
	}

	public void resumeSound (long soundId) {
		int sourceId = soundIdToSource.get(soundId, -1);
		if (sourceId != -1 && alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED) alSourcePlay(sourceId);
	}

	public void setSoundGain (long soundId, float volume) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setSoundLooping (long soundId, boolean looping) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) alSourcei(sourceId, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setSoundPitch (long soundId, float pitch) {
		Integer sourceId = soundIdToSource.get(soundId);
		if (sourceId != null) AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setSoundPan (long soundId, float pan, float volume) {
		int sourceId = soundIdToSource.get(soundId, -1);
		if (sourceId != -1) {
			AL10.alSource3f(sourceId, AL10.AL_POSITION, GdxMathUtils.cos((pan - 1) * GdxMathUtils.PI / 2), 0,
				GdxMathUtils.sin((pan + 1) * GdxMathUtils.PI / 2));
			AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
		}
	}

	public void dispose () {
		if (noDevice) return;
		for (int i = 0, n = allSources.size; i < n; i++) {
			int sourceID = allSources.get(i);
			int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
			if (state != AL_STOPPED) alSourceStop(sourceID);
			alDeleteSources(sourceID);
		}

		sourceToSoundId.clear();
		soundIdToSource.clear();

		/*AL.destroy();
		while (AL.isCreated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}*/
	}

	public AudioDevice newAudioDevice (int sampleRate, final boolean isMono) {
		if (noDevice) return new AudioDevice() {
			@Override
			public void writeSamples (float[] samples, int offset, int numSamples) {
			}

			@Override
			public void writeSamples (short[] samples, int offset, int numSamples) {
			}

			@Override
			public void setVolume (float volume) {
			}

			@Override
			public boolean isMono () {
				return isMono;
			}

			@Override
			public int getLatency () {
				return 0;
			}

			@Override
			public void dispose () {
			}
		};
		return new OpenALAudioDevice(this, sampleRate, isMono, deviceBufferSize, deviceBufferCount);
	}

	/*public AudioRecorder newAudioRecorder (int samplingRate, boolean isMono) {
		if (noDevice) return new AudioRecorder() {
			@Override
			public void read (short[] samples, int offset, int numSamples) {
			}

			@Override
			public void dispose () {
			}
		};
		return new JavaSoundAudioRecorder(samplingRate, isMono);
	}*/

	/** Retains a list of the most recently played sounds and stops the sound played least recently if necessary for a new sound to
	 * play */
	protected void retain (OpenALSound sound, boolean stop) {
		// Move the pointer ahead and wrap
		mostRecetSound++;
		mostRecetSound %= recentSounds.length;

		if (stop) {
			// Stop the least recent sound (the one we are about to bump off the buffer)
			if (recentSounds[mostRecetSound] != null) recentSounds[mostRecetSound].stop();
		}

		recentSounds[mostRecetSound] = sound;
	}

	/** Removes the disposed sound from the least recently played list */
	public void forget (OpenALSound sound) {
		for (int i = 0; i < recentSounds.length; i++) {
			if (recentSounds[i] == sound) recentSounds[i] = null;
		}
	}
}
