package org.bimserver.gltf;

/******************************************************************************
 * Copyright (C) 2009-2018  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see {@literal<http://www.gnu.org/licenses/>}.
 *****************************************************************************/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bimserver.emf.Schema;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.PluginContext;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.serializers.AbstractSerializerPlugin;
import org.bimserver.plugins.serializers.Serializer;
import org.bimserver.shared.exceptions.PluginException;

public class BinaryGltfSerializerPlugin extends AbstractSerializerPlugin {

	private byte[] vertexColorFragmentShaderBytes;
	private byte[] vertexColorVertexShaderBytes;
	private byte[] materialColorFragmentShaderBytes;
	private byte[] materialColorVertexShaderBytes;

	@Override
	public void init(PluginContext pluginContext) throws PluginException {
		Path vertexColorFragmentShaderPath = pluginContext.getRootPath().resolve("shaders/fragmentcolor.shader");
		Path vertexColorVertexShaderPath = pluginContext.getRootPath().resolve("shaders/vertexcolor.shader");
		Path materialColorFragmentShaderPath = pluginContext.getRootPath().resolve("shaders/fragmentmaterial.shader");
		Path materialColorVertexShaderPath = pluginContext.getRootPath().resolve("shaders/vertexmaterial.shader");
		
		try {
			vertexColorFragmentShaderBytes = Files.readAllBytes(vertexColorFragmentShaderPath);
			vertexColorVertexShaderBytes = Files.readAllBytes(vertexColorVertexShaderPath);
			materialColorFragmentShaderBytes = Files.readAllBytes(materialColorFragmentShaderPath);
			materialColorVertexShaderBytes = Files.readAllBytes(materialColorVertexShaderPath);
		} catch (IOException e) {
			throw new PluginException(e);
		}
	}

	@Override
	public Serializer createSerializer(PluginConfiguration plugin) {
		return new BinaryGltfSerializer(vertexColorFragmentShaderBytes, vertexColorVertexShaderBytes, materialColorFragmentShaderBytes, materialColorVertexShaderBytes);
	}

	@Override
	public Set<Schema> getSupportedSchemas() {
		return Collections.singleton(Schema.IFC2X3TC1);
	}

	@Override
	public Set<String> getRequiredGeometryFields() {
		Set<String> set = new HashSet<>();
		set.add("indices");
		set.add("vertices");
		set.add("normals");
		set.add("colorsQuantized");
		return set;
	}

	@Override
	public String getDefaultExtension() {
		return "glb";
	}

	@Override
	public String getDefaultContentType() {
		return "model/gltf+binary";
	}

	@Override
	public String getOutputFormat(Schema schema) {
		return SchemaName.GLTF_BIN_1_0.name();
	}
}